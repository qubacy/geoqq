package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type GeoChatStorage struct {
	pool *pgxpool.Pool
}
