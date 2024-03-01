package postgre

import (
	"context"
	"fmt"
	"geoqq/pkg/utility"
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

func Test_pgxpool_Conn_Config(t *testing.T) {
	connStr := "user=postgres password=admin database=geoqq host=127.0.0.1 port=5433"
	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	poolConfig := pool.Config()
	fmt.Println("Max Conns:", poolConfig.MaxConns)
	fmt.Println("Min Conns:", poolConfig.MinConns)
	fmt.Println("Max Conn Lifetime:", poolConfig.MaxConnLifetime)
	fmt.Println("Max Conn Lifetime Jitter:", poolConfig.MaxConnLifetimeJitter)
	fmt.Println("Max Conn Idle Time:", poolConfig.MaxConnIdleTime)
	fmt.Println("Health Check Period:", poolConfig.HealthCheckPeriod)
	fmt.Println("Lazy Connect:", poolConfig.LazyConnect)
	//...
}

func Test_pgxpool_Conn_Stat(t *testing.T) {
	connStr := "user=postgres password=admin database=geoqq host=127.0.0.1 port=5433"
	pool, err := pgxpool.Connect(context.Background(), connStr)
	if err != nil {
		t.Errorf("Connect to database failed. Err: %v", err)
	}

	var sep = " ="
	poolStat := pool.Stat()
	fmt.Println(utility.GetFunctionName(poolStat.AcquireCount)+sep, poolStat.AcquireCount())
	fmt.Println(utility.GetFunctionName(poolStat.AcquireDuration)+sep, poolStat.AcquireDuration())
	fmt.Println(utility.GetFunctionName(poolStat.AcquiredConns)+sep, poolStat.AcquiredConns())
	fmt.Println(utility.GetFunctionName(poolStat.CanceledAcquireCount)+sep, poolStat.CanceledAcquireCount())
	fmt.Println(utility.GetFunctionName(poolStat.ConstructingConns)+sep, poolStat.ConstructingConns())
	fmt.Println(utility.GetFunctionName(poolStat.EmptyAcquireCount)+sep, poolStat.EmptyAcquireCount())
	fmt.Println(utility.GetFunctionName(poolStat.IdleConns)+sep, poolStat.IdleConns())
	fmt.Println(utility.GetFunctionName(poolStat.MaxConns)+sep, poolStat.MaxConns())
	fmt.Println(utility.GetFunctionName(poolStat.MaxIdleDestroyCount)+sep, poolStat.MaxIdleDestroyCount())
	fmt.Println(utility.GetFunctionName(poolStat.MaxLifetimeDestroyCount)+sep, poolStat.MaxLifetimeDestroyCount())
	fmt.Println(utility.GetFunctionName(poolStat.NewConnsCount)+sep, poolStat.NewConnsCount())
	fmt.Println(utility.GetFunctionName(poolStat.TotalConns)+sep, poolStat.TotalConns())
	//...
}
