package postgre

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
	"context"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

func queryRow(ctx context.Context, pool *pgxpool.Pool, tmpl string, args ...any) (pgx.Row, error) {
	row, err := wrappedPgxpool.QueryRowWithConnectionAcquire(pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx, tmpl+`;`,
				args...)
		})
	return row, err
}
