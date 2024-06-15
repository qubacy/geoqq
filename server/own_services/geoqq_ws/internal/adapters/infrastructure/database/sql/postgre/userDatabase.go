package postgre

import (
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
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

// public
// -----------------------------------------------------------------------

func (s *UserDatabase) UpdateUserLocation(ctx context.Context,
	userId uint64, lon, lat float64) error {

	sourceFunc := s.UpdateUserLocation
	c, err := s.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	defer c.Release()

	cmdTag, err := c.Exec(ctx,
		template.UpdateUserLocation+`;`, lon, lat)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
