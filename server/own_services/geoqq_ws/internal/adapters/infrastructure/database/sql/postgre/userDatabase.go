package postgre

import (
	domain "common/pkg/domain/geoqq"
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/table"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/common"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"

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

func (s *UserDatabase) GetUserLocation(ctx context.Context, userId uint64) (
	*domain.UserLocation, error) {

	sourceFunc := s.GetUserLocation
	row := s.pool.QueryRow(ctx,
		template.GetUserLocationWithId+`;`, userId)

	ul := domain.UserLocation{}
	err := row.Scan(&ul.UserId, &ul.Lon, &ul.Lat, &ul.Time)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return &ul, nil
}

func (s *UserDatabase) GetUserEntryById(ctx context.Context, userId uint64) (
	*table.UserEntry, error) {

	sourceFunc := s.GetUserEntryById
	row := s.pool.QueryRow(ctx, template.SelectUserEntryById+`;`, userId)

	ue := table.UserEntry{}
	err := row.Scan(&ue.Id, &ue.Login,
		&ue.HashPassword, &ue.HashUpdToken,
		&ue.SignUpTime, &ue.SignInTime,
		&ue.LastActionTime)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return &ue, nil
}

func (s *UserDatabase) GetPublicUserById(ctx context.Context,
	userId, targetUserId uint64) (*domain.PublicUser, error) {

	return nil, constErrors.ErrNotImplemented
}

func (s *UserDatabase) GetTransformedPublicUserById(ctx context.Context,
	userId uint64, targetUserId uint64,
	transform database.PublicUserTransform,
) (*domain.PublicUser, error) {

	return nil, constErrors.ErrNotImplemented
}
