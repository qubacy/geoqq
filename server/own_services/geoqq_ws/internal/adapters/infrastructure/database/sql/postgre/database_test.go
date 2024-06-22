package postgre

import (
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"fmt"
	"log"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"testing"
	"time"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v4/pgxpool"
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
	startupTimeout  = 15 * time.Second
	occurrenceCount = 3
)

// -----------------------------------------------------------------------

var (
	postgreHost        = ""
	postgrePort uint16 = 5432

	// ***

	postgreExternalHost        = ""
	postgreExternalPort uint16 = 0

	pathToMigrations = "/common/storage/domain/sql/postgre/migrations" // will be changed!
)

var (
	postgresContainer *postgres.PostgresContainer
	migrateContainer  testcontainers.Container
)

func createConnString(fromExternal bool) string {
	host := postgreHost
	port := postgrePort

	if fromExternal {
		host = postgreExternalHost
		port = postgreExternalPort
	}

	return fmt.Sprintf("postgres://%v:%v@"+
		"%v:%v/%v?sslmode=disable",
		postgreUsername, postgrePassword,
		host, port, postgreDbName)
}

const (
	userEntryCount = 10
)

// -----------------------------------------------------------------------

type MateChat struct {
	id           uint64
	firstUserId  uint64
	secondUserId uint64
}

type Mate struct {
	id           uint64
	firstUserId  uint64
	secondUserId uint64
}

var (
	userIds   = []uint64{}
	mateIds   = []Mate{}
	mateChats = []MateChat{}
)

// migrations volume
// -----------------------------------------------------------------------

type ContainerMountSourceMigrations struct{}

func (c ContainerMountSourceMigrations) Source() string {
	return pathToMigrations
}

func (c ContainerMountSourceMigrations) Type() testcontainers.MountType {
	return testcontainers.MountTypeBind // deprecated!
}

// -----------------------------------------------------------------------

func setup() {
	startPrefix := "\t\t\t start "

	ctx := context.Background()
	var err error = nil

	commonNet, err := network.New(ctx, network.WithDriver("bridge"))
	if err != nil {
		log.Fatalf("create common net err: %s", err)
	}

	// postgre
	log.Println(startPrefix + "`postgre`")

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
		postgreExternalPort64, _ := strconv.ParseUint(parts[1], 10, 16)
		postgreExternalPort = uint16(postgreExternalPort64)

		// ***

		log.Printf("postgre host: %v\n", postgreHost)
		log.Printf("postgre db name: %v\n", postgreDbName)
		log.Printf("postgre external host: %v\n", postgreExternalHost)
		log.Printf("postgre external port: %v\n", postgreExternalPort)
	}

	// migrate
	log.Println(startPrefix + "`migrate`")

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
				createConnString(false),
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

			containerState, err := migrateContainer.State(ctx)
			if err != nil {
				log.Fatalf("migrate container inspect with err: %v", err)
			}

			// ***

			log.Printf("current status migrate container: %v\n",
				containerState.Status)
			if containerState.Status == "exited" {
				break
			}
		}
	}

	// inflate

	if err = inflate(); err != nil {
		log.Fatalf("inflate with err: %v", err)
	}

	// init db (connection pool)

	if err = initDb(); err != nil {
		log.Fatalf("init db with err: %v", err)
	}

	log.Println("setup [OK]")
}

func inflate() error {
	ctx := context.Background()
	pool, err := pgxpool.Connect(ctx, createConnString(true))
	if err != nil {
		return err
	}

	// add user entry

	for i := 0; i < userEntryCount; i++ {
		randomLogin := uuid.NewString()
		randomHashPass := uuid.NewString()

		row := pool.QueryRow(ctx,
			template.InsertUserEntryWithoutHashUpdToken,
			randomLogin, randomHashPass)

		var userId uint64 = 0
		if err := row.Scan(&userId); err != nil {
			return err
		}

		userIds = append(userIds, userId)
	}

	// add user location

	for i := 0; i < len(userIds); i++ {
		cmdTag, err := pool.Exec(ctx,
			template.InsertUserLocationNoReturningId,
			userIds[i], rand.Float64(), // lon
			rand.Float64()) // lat

		if !cmdTag.Insert() {
			return ErrInsertFailed
		}
		if err != nil {
			return err
		}
	}

	// add mate/mate chats

	for i := 0; i < userEntryCount/2; i += 2 {
		row := pool.QueryRow(ctx,
			template.InsertMate,
			userIds[i], userIds[i+1])

		var id uint64 = 0
		if err := row.Scan(&id); err != nil {
			return err
		}
		mateIds = append(mateIds, Mate{
			id:           id,
			firstUserId:  userIds[i],
			secondUserId: userIds[i+1],
		})

		row = pool.QueryRow(ctx,
			template.InsertMateChat,
			userIds[i], userIds[i+1])

		if err := row.Scan(&id); err != nil {
			return err
		}
		mateChats = append(mateChats, MateChat{
			id:           id,
			firstUserId:  userIds[i],
			secondUserId: userIds[i+1],
		})
	}

	log.Printf("new users: %v", userIds)
	return nil
}

func teardown() {
	ctx := context.Background()
	var err error = nil

	if err = postgresContainer.Terminate(ctx); err != nil {
		log.Printf("failed to terminate postgre container: %s", err)
	}

	if err = migrateContainer.Terminate(ctx); err != nil {
		log.Printf("failed to terminate migrate container: %s", err)
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

var (
	db *Database = nil
)

func initDb() error {
	var err error
	db, err = New(context.Background(), Dependencies{
		Username:     postgreUsername,
		Password:     postgrePassword,
		Host:         postgreExternalHost,
		Port:         postgreExternalPort,
		DatabaseName: postgreDbName,
	})
	if err != nil {
		log.Fatal(err)
	}

	return nil
}

// -----------------------------------------------------------------------

func Test_InsertMateMessage(t *testing.T) {
	ctx := context.Background()

	// ***

	index := rand.Int63n(int64(len(mateChats)))
	mc := mateChats[int(index)]

	textMessage := "text_text_text"
	mateMsgId, err := db.InsertMateMessage(ctx, mc.id,
		mc.firstUserId, textMessage)
	if err != nil {
		t.Error(err)
		return
	}
	log.Printf("mate message id: %v\n", mateMsgId)

	mm, err := db.GetMateMessageById(ctx, mateMsgId)
	if err != nil {
		t.Error(err)
		return
	}
	log.Printf("mate message: %v\n", mm)

	if mm.ChatId != mc.id { // !
		t.Errorf("got: %v, want: %v", mm.ChatId, mc.id)
		return
	}
	if mm.Text != textMessage {
		t.Errorf("got: %v, want: %v", mm.Text, textMessage)
		return
	}
	if mm.UserId != mc.firstUserId {
		t.Errorf("got: %v, want: %v", mm.UserId, mc.firstUserId)
		return
	}

	// ***

	jsonBytes, err := json.Marshal(mm)
	if err != nil {
		t.Error(err)
		return
	}

	log.Printf("json mate message: %v",
		string(jsonBytes))
}

func Test_GetUserLocation(t *testing.T) {
	ctx := context.Background()
	uc, err := db.GetUserLocation(ctx, userIds[0])
	if err != nil {
		t.Error(err)
		return
	}

	log.Printf("user %v has loc: %v",
		userIds[0], uc)
}
