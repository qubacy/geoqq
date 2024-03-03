package postgre

import (
	"context"
	"errors"
	"fmt"
	"testing"

	"github.com/jackc/pgx/v4/pgxpool"
)

func Test_GetIncomingMateRequestsForUser(t *testing.T) {
	connStr := createConnectionString(Dependencies{
		User: "postgres", Password: "admin",
		DbName: "geoqq", Host: "127.0.0.1", Port: 5432, // TODO: from config!
	})

	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	storage := newMateRequestStorage(pool)
	mateRequests, err := storage.GetAllWaitingMateRequestsForUser(
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
		DbName: "geoqq", Host: "127.0.0.1", Port: 5432, // TODO: from config!
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

// -----------------------------------------------------------------------

func Test_errors_Join(t *testing.T) {
	err := errors.Join(errors.New("one"), errors.New("two"))

	if err != nil {
		fmt.Println(err)
	}
}
