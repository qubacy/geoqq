package redis

import (
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/output/cache"
	"log"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"testing"
	"time"

	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/redis"
	"github.com/testcontainers/testcontainers-go/wait"
)

var (
	redisContainer    *redis.RedisContainer
	redisExternalHost string
	redisExternalPort uint16

	tempDb *Cache
)

const (
	userCount = 10
)

// -----------------------------------------------------------------------

func TestMain(m *testing.M) {
	setup()
	rc := m.Run()
	teardown()

	os.Exit(rc)
}

func setup() {
	startPrefix := "\t\t\t start "

	ctx := context.Background()
	var err error

	// redis
	log.Println(startPrefix + "`redis`")

	{
		err := utl.RunFuncsRetErr(
			func() error {
				redisContainer, err = redis.RunContainer(
					ctx,
					testcontainers.WithImage("redis:alpine3.20"),
					testcontainers.WithWaitStrategyAndDeadline(
						60*time.Second,
						wait.ForLog("Ready to accept connections tcp")),
				)
				return err
			},
			func() error {
				time.Sleep(2500 * time.Millisecond) // ?
				endpoint, err := redisContainer.Endpoint(ctx, "")
				if err != nil {
					return err
				}

				parts := strings.Split(endpoint, ":")
				redisExternalHost = parts[0]
				redisExternalPort64, err := strconv.ParseUint(parts[1], 10, 16)
				if err != nil {
					return err
				}

				redisExternalPort = uint16(redisExternalPort64)
				return nil
			})
		if err != nil {
			log.Fatalf("redis container err: %s", err)

			// err = `port not found`
		}

		// ***

		log.Printf("redis external host: %v\n", redisExternalHost)
		log.Printf("redis external port: %v\n", redisExternalPort)
	}

	// create conn

	tempDb, err = New(ctx, &Params{
		Host:     redisExternalHost,
		Port:     redisExternalPort,
		User:     "",
		Password: "",
		DbIndex:  0, // no matter what!
	})
	if err != nil {
		log.Fatalf("failed to create cache: %s", err)
	}
}

func teardown() {
	ctx := context.Background()
	var err error = nil

	if err = redisContainer.Terminate(ctx); err != nil {
		log.Printf("failed to terminate container: %s", err)
	}
}

// -----------------------------------------------------------------------

// Latitude - Широта
// Longitude - Долгота

func Test_SomeScenario(t *testing.T) {
	ctx := context.Background()
	var wantLoc = cache.Location{Lat: 56.01, Lon: 92.85}
	var userId uint64 = 1

	{
		err := tempDb.AddUserLocation(ctx, userId, wantLoc)
		if err != nil {
			t.Error(err)
			return
		}
	}

	// ***

	{
		exists, gotLoc, err := tempDb.GetUserLocation(ctx, userId)
		if err != nil {
			t.Error(err)
			return
		}

		if !exists {
			t.Errorf("user with id %v not found", userId)
			return
		}

		prec := 0.001
		if (wantLoc.Lat-gotLoc.Lat) > prec || (wantLoc.Lon-gotLoc.Lon) > prec {
			t.Errorf("got loc: %v, want loc: %v", gotLoc, wantLoc)
			return
		}

		log.Printf("loc: %v", wantLoc)
	}
}

func Test_SomeScenario_1(t *testing.T) {
	ctx := context.Background()
	var userId uint64

	for ; userId < userCount; userId++ {
		err := tempDb.AddUserLocation(ctx, userId, genLocation())
		if err != nil {
			t.Error(err)
			return
		}
	}

	// ***

	var targetUserId uint64 = uint64(userCount / 2)
	if err := tempDb.RemoveAllForUser(ctx, targetUserId); err != nil {
		t.Error(err)
		return
	}

	exists, _, err := tempDb.GetUserLocation(ctx, targetUserId)
	if err != nil {
		t.Error(err)
		return
	}

	if exists {
		t.Errorf("got: %v, want: %v", exists, false)
		return
	}
}

func Test_AddUserLocation(t *testing.T) {
	ctx := context.Background()
	const userId = uint64(1)

	for i := 0; i < 3; i++ {
		loc := genLocation()
		if err := tempDb.AddUserLocation(ctx, userId, loc); err != nil {
			t.Error(err)
			return
		}
	}

	// ***

	_, _, err := tempDb.GetUserLocation(ctx, userId) // !
	if err != nil {
		t.Error(err)
		return
	}
}

func Test_SearchUsersNearby(t *testing.T) {
	ctx := context.Background()
	var userId uint64

	for ; userId < userCount; userId++ {
		loc := genLocation()
		if err := tempDb.AddUserLocation(ctx, userId, loc); err != nil {
			t.Error(err)
			return
		}
	}

	// ***

	loc := genLocation()
	userIds, err := tempDb.SearchUsersNearby(ctx, loc, 200_000) // meters
	if err != nil {
		t.Error(err)
		return
	}

	log.Printf("%v", userIds)
}

// private
// -----------------------------------------------------------------------

func genLocation() cache.Location {
	return cache.Location{
		Lat: float64(rand.Int63n(5)),
		Lon: float64(rand.Int63n(5))}
}
