package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type MateRequestStorage struct {
	pool *pgxpool.Pool
}
