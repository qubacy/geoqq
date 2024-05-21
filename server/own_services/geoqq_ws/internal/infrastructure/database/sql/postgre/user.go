package postgre

import (
	"context"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserDatabase struct {
	pool *pgxpool.Pool
}

func newUserDatabase(pool *pgxpool.Pool) *UserDatabase {
	return &UserDatabase{
		pool: pool,
	}
}

func (s *UserDatabase) InsertUserLocation(ctx context.Context,
	lon, lat float64, radius uint64) error {

	return ErrNotImplemented
}
