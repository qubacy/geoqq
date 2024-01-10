package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type ImageStorage struct {
	pool *pgxpool.Pool
}
