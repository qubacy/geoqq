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

func (s *MateStorage) AreMates(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(s.AreMates, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "Mate" 
			WHERE "FirstUserId" = $1
				AND "SecondUserId" = $2;`,
		firstUserId, secondUserId)

	var count = 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.NewFuncError(s.AreMates, err)
	}
	if count > 1 { // impossible!
		return false, ErrUnexpectedResult
	}

	return count == 1, nil
}
