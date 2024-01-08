package postgre

import (
	"context"
	"testing"

	"github.com/jackc/pgx/v4/pgxpool"
)

func Test_pgxpool_Connect(t *testing.T) {
	connStr := "user=postgres password=admin database=geoqq host=127.0.0.1 port=5432"
	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	conn, err := pool.Acquire(context.Background())
	if err != nil {
		t.Errorf("Acquire to database failed. Err: %v", err)
	}
	defer conn.Release()
}
