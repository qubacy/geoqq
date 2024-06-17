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
)

const (
	postgreDbName    = "domain"
	postgreUsername  = "postgre"
	postgrePassword  = "postgre"
	pathToMigrations = "../../../../../../common/storage/domain/sql/postgre/migrations"
)

var (
	postgreHost        = ""
	postgrePort uint16 = 0
)

var (
	startupTimeout  = 15 * time.Second
	occurrenceCount = 3
)

var postgresContainer *postgres.PostgresContainer
var migrateContainer testcontainers.Container

// -----------------------------------------------------------------------

func setup() {
	ctx := context.Background()
	var err error

	// postgre
	log.Println("start `postgre`")

	{
		postgresContainer, err = postgres.RunContainer(
			ctx,
			testcontainers.WithImage("docker.io/postgres:16.3-alpine3.19"),
			postgres.WithUsername(postgreUsername),
			postgres.WithPassword(postgrePassword),
			postgres.WithDatabase(postgreDbName),

			testcontainers.WithEnv(map[string]string{
				"POSTGRES_HOST_AUTH_METHOD": "md5"}))

		// testcontainers.WithWaitStrategy(
		// 	wait.ForLog("database system is ready to accept connections").
		// 		WithOccurrence(occurrenceCount).
		// 		WithStartupTimeout(startupTimeout)))

		time.Sleep(5 * time.Second)

		var endpoint string
		utl.RunFuncsRetErr(
			func() error { return err },
			func() error {
				endpoint, err = postgresContainer.Endpoint(ctx, "")
				return err
			})
		if err != nil {
			log.Fatalf("postgres container err: %s", err)
		}

		parts := strings.Split(endpoint, ":")
		postgreHost = parts[0]
		postgrePort64, _ := strconv.ParseUint(parts[1], 10, 16)
		postgrePort = uint16(postgrePort64)

		log.Printf("postgre host: %v\n", postgreHost)
		log.Printf("postgre port: %v\n", postgrePort)
	}

	// migrate
	log.Println("start `migrate`")

	{
		reqContainer := testcontainers.ContainerRequest{
			Image: "migrate/migrate",
			Cmd: []string{
				fmt.Sprintf("-path=%v", pathToMigrations),
				"-database",
				fmt.Sprintf("postgres://%v:%v@"+
					"%v:%v/%v?sslmode=disable",
					postgreUsername, postgrePassword,
					postgreHost, postgrePort,
					postgreDbName),
				"up",
			},
		}
		migrateContainer, err = testcontainers.GenericContainer(
			ctx,
			testcontainers.GenericContainerRequest{
				ContainerRequest: reqContainer,
				Started:          true,
			})
		if err != nil {
			log.Fatalf("migrate container err: %s", err)
		}
	}

	// inflate
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
		Host:         postgreHost,
		Port:         postgrePort,
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
