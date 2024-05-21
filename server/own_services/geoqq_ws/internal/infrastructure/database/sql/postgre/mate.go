package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type MateDatabase struct {
	pool *pgxpool.Pool
}

func newMateDatabase(pool *pgxpool.Pool) *MateDatabase {
	return &MateDatabase{
		pool: pool,
	}
}
