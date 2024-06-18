package postgre

import (
	utl "common/pkg/utility"
	"context"
	"fmt"
	"log"
	"os"
	"strconv"
	"strings"
	"testing"
	"time"

	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/postgres"
	"github.com/testcontainers/testcontainers-go/network"
	"github.com/testcontainers/testcontainers-go/wait"
)

const (
	postgreDbName   = "domain"
	postgreUsername = "postgre"
	postgrePassword = "postgre"
)

var (
	postgreHost        = ""
	postgrePort uint16 = 5432

	postgreExternalHost        = ""
	postgreExternalPort uint16 = 0
	pathToMigrations           = "/common/storage/domain/sql/postgre/migrations" // will be changed!
)

var (
	startupTimeout  = 15 * time.Second
	occurrenceCount = 3
)

var (
	postgresContainer *postgres.PostgresContainer = nil
	migrateContainer  testcontainers.Container    = nil
)

// volume
// -----------------------------------------------------------------------

type ContainerMountSourceMigrations struct{}

func (c ContainerMountSourceMigrations) Source() string {
	return pathToMigrations
}

func (c ContainerMountSourceMigrations) Type() testcontainers.MountType {
	return testcontainers.MountTypeBind
}

// -----------------------------------------------------------------------

func setup() {
	startPrefix := "\t\t\t"
	ctx := context.Background()
	var err error = nil

	commonNet, err := network.New(ctx, network.WithDriver("bridge"))
	if err != nil {
		log.Fatalf("create common net err: %s", err)
	}

	// postgre
	log.Println(startPrefix + "start `postgre`")

	{
		postgresContainer, err = postgres.RunContainer(
			ctx,
			testcontainers.WithImage("docker.io/postgres:16.3-alpine3.19"),
			postgres.WithUsername(postgreUsername),
			postgres.WithPassword(postgrePassword),
			postgres.WithDatabase(postgreDbName),

			testcontainers.WithEnv(map[string]string{"POSTGRES_HOST_AUTH_METHOD": "md5"}),
			network.WithNetwork([]string{}, commonNet),

			testcontainers.WithWaitStrategy(
				wait.ForLog("listening").
					WithOccurrence(occurrenceCount).
					WithStartupTimeout(startupTimeout))) // ?
		time.Sleep(250 * time.Millisecond)

		var endpoint string
		utl.RunFuncsRetErr(
			func() error { return err },
			func() error {
				containerJson, err := postgresContainer.Inspect(ctx)
				if err == nil {
					postgreHost = containerJson.Config.Hostname
				}
				return err
			}, func() error {
				endpoint, err = postgresContainer.Endpoint(ctx, "")
				return err
			})
		if err != nil {
			log.Fatalf("postgres container err: %s", err)
		}

		parts := strings.Split(endpoint, ":")
		postgreExternalHost = parts[0]
		postgrePort64, _ := strconv.ParseUint(parts[1], 10, 16)
		postgreExternalPort = uint16(postgrePort64)

		// ***

		log.Printf("postgre host: %v\n", postgreHost)
		log.Printf("postgre db name: %v\n", postgreDbName)
		log.Printf("postgre external host: %v\n", postgreExternalHost)
		log.Printf("postgre external port: %v\n", postgreExternalPort)
	}

	// migrate
	log.Println(startPrefix + "start `migrate`")

	{
		wd, _ := os.Getwd()
		wd = strings.Replace(wd, "\\", "/", -1)

		ownServicesDir := "own_services"
		index := strings.Index(wd, ownServicesDir)
		wd = wd[:index+len(ownServicesDir)]
		pathToMigrations = wd + pathToMigrations
		log.Printf("path to migrations: %v", pathToMigrations)

		// ***

		reqContainer := testcontainers.ContainerRequest{
			Image: "migrate/migrate",
			Cmd: []string{
				fmt.Sprintf("-path=%v", "/migrations/"),
				"-database",
				fmt.Sprintf("postgres://%v:%v@"+
					"%v:%v/%v?sslmode=disable",
					postgreUsername, postgrePassword,
					postgreHost, postgrePort,
					postgreDbName),
				"up",
			},
			Networks: []string{commonNet.Name}, // ?
			Mounts: []testcontainers.ContainerMount{{
				Source: ContainerMountSourceMigrations{},
				Target: testcontainers.ContainerMountTarget("/migrations/"),
			}},
		}
		migrateContainer, err = testcontainers.GenericContainer(
			ctx,
			testcontainers.GenericContainerRequest{
				ContainerRequest: reqContainer,
				Started:          true,
			})
		if err != nil {
			log.Fatalf("migrate container err: %v", err)
		}

		for {
			time.Sleep(250 * time.Millisecond)

			containerJson, err := migrateContainer.Inspect(ctx)
			if err != nil {
				log.Fatalf("migrate container inspect with err: %v", err)
			}

			if containerJson.State.Status == "exited" {
				break
			}
		}
	}

	// inflate

	log.Println("setup [OK]")
}

func teardown() {
	ctx := context.Background()
	var err error

	if err = postgresContainer.Terminate(ctx); err != nil {
		log.Fatalf("failed to terminate container: %s", err)
	}

	if err = migrateContainer.Terminate(ctx); err != nil {
		log.Fatalf("failed to terminate container: %s", err)
	}

	//...
}

func TestMain(m *testing.M) {
	setup()
	rc := m.Run()
	teardown()

	os.Exit(rc)
}

// public
// -----------------------------------------------------------------------

func Test_InsertMateMessage(t *testing.T) {
	ctx := context.Background()
	db, err := New(context.Background(), Dependencies{
		Username:     postgreUsername,
		Password:     postgrePassword,
		Host:         postgreExternalHost,
		Port:         postgreExternalPort,
		DatabaseName: postgreDbName,
	})
	if err != nil {
		t.Error(err)
		return
	}

	mateMsgId, err := db.InsertMateMessage(ctx, 1, 1, "text")
	if err != nil {
		t.Error(err)
		return
	}

	log.Printf("mate message id: %v\n", mateMsgId)
}
