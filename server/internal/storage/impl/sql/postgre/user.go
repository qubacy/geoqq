package postgre

import (
	"context"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newUserStorage(pool *pgxpool.Pool) *UserStorage {
	return &UserStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (self *UserStorage) HasUserWithName(ctx context.Context, value string) (bool, error) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.CreateCustomError(self.HasUserWithName, err)
	}
	defer conn.Release()

	return false, nil
}

func (self *UserStorage) InsertUser(ctx context.Context, username,
	hashPassword, hashUpdToken string) (uint64, error) {
	return 0, nil
}

func (self *UserStorage) HasUserByCredentials(ctx context.Context, username, hashPassword string) (bool, error) {
	return false, nil
}

func (self *UserStorage) UpdateUserLocation(ctx context.Context, id uint64,
	longitude, latitude float64) error {
	return nil
}
