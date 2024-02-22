package postgre

import (
	"context"
	"fmt"
	"testing"

	"github.com/jackc/pgx/v4/pgxpool"
)

func Test_GetIncomingMateRequestsForUser(t *testing.T) {
	connStr := createConnectionString(Dependencies{
		User: "postgres", Password: "admin",
		DbName: "geoqq", Host: "127.0.0.1", Port: 5433, // TODO: from config!
	})

	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	storage := newMateRequestStorage(pool)
	mateRequests, err := storage.GetIncomingMateRequestsForUser(
		context.Background(), 2)
	if err != nil {
		t.Error(err)
	}

	for i := range mateRequests {
		fmt.Println(mateRequests[i])
	}
}

func Test_GetMateRequestById(t *testing.T) {
	connStr := createConnectionString(Dependencies{
		User: "postgres", Password: "admin",
		DbName: "geoqq", Host: "127.0.0.1", Port: 5433, // TODO: from config!
	})

	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	storage := newMateRequestStorage(pool)
	mateRequest, err := storage.GetMateRequestById(context.Background(), 1)
	if err != nil {
		t.Error(err)
	}

	fmt.Println(mateRequest)
}
