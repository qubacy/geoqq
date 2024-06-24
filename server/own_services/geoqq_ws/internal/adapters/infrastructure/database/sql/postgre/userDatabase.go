package postgre

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/common"
	"geoqq_ws/internal/application/domain"

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
		template.UpdateUserLocation+`;`, lon, lat, userId)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	if !cmdTag.Update() {
		return common.ErrUpdateFailed
	}

	return nil
}

func (s *UserDatabase) GetUserLocation(ctx context.Context, userId uint64) (
	*domain.UserLocation, error) {

	sourceFunc := s.GetUserLocation
	row := s.pool.QueryRow(ctx,
		template.GetUserLocationWithId, userId)

	ul := domain.UserLocation{}
	err := row.Scan(&ul.UserId, &ul.Lon, &ul.Lat, &ul.Time)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return &ul, nil
}

func (s *UserDatabase) HasUserWithId(ctx context.Context,
	userId uint64) (bool, error) {

	sourceFunc := s.HasUserWithId
	row := s.pool.QueryRow(ctx, template.HasUserWithId+`;`, userId)

	var has bool
	has, err := wrappedPgxpool.ScanPrimitive[bool](row, sourceFunc)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}

	return has, nil
}
