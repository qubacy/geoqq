package postgre

import (
	"context"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type MateStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateStorage(pool *pgxpool.Pool) *MateStorage {
	return &MateStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (ms *MateStorage) AreMates(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (bool, error) {
	conn, err := ms.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(ms.AreMates, err)
	}
	defer conn.Release()

	conn.QueryRow(ctx, ``, firstUserId, secondUserId)
	return false, nil
}
