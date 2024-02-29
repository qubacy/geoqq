package postgre

import (
	"context"
	"fmt"
	"testing"

	"github.com/jackc/pgx/v4/pgxpool"
)

// TODO: read from config!

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

func Test_createConnectionString(t *testing.T) {
	connStr := createConnectionString(Dependencies{
		User: "postgres", Password: "admin",
		DbName: "geoqq", Host: "127.0.0.1", Port: 5432,
	})
	fmt.Println("conn str:", connStr)
}

// request to database
// -----------------------------------------------------------------------

func Test_pgxpool_Conn_Exec(t *testing.T) {
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

func Test_pgxpool_Conn_Query(t *testing.T) {
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

// -----------------------------------------------------------------------

func Test_pgxpool_Conn(t *testing.T) {
	connStr := "user=postgres password=admin database=geoqq host=127.0.0.1 port=5433"
	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	poolConfig := pool.Config()
	fmt.Println("Max conns:", poolConfig.MaxConns)
	fmt.Println("Min conns:", poolConfig.MinConns)
	fmt.Println("Health check period:", poolConfig.HealthCheckPeriod)
	fmt.Println("Lazy connect:", poolConfig.LazyConnect)
}
