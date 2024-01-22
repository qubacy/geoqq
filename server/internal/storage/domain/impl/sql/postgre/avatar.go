package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type AvatarStorage struct {
	pool *pgxpool.Pool
}
