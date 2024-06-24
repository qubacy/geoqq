package background

import (
	"common/pkg/logger"
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/common"
	"math/rand"

	"github.com/jackc/pgconn"
	"github.com/jackc/pgx/v4/pgxpool"
)

type UserDatabase struct {
	pool          *pgxpool.Pool
	chWithQueries []chan wrappedPgxpool.BgrQueryWrapper
}

func newUserDatabase(pool *pgxpool.Pool) *UserDatabase {
	return &UserDatabase{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (u *UserDatabase) UpdateBgrLastActionTimeForUser(id uint64) {
	chIndex := rand.Intn(len(u.chWithQueries))
	u.chWithQueries[chIndex] <- func(conn *pgxpool.Conn, ctx context.Context) error {

		// TODO:!!!

		return nil
	}
}

func (u *UserDatabase) UpdateBgrLocationForUser(id uint64, lon, lat float64) {
	chIndex := rand.Intn(len(u.chWithQueries))
	u.chWithQueries[chIndex] <- func(conn *pgxpool.Conn, ctx context.Context) error {
		var (
			cmdTag pgconn.CommandTag
			err    error = nil
		)
		err = utl.RunFuncsRetErr(
			func() error {
				cmdTag, err = conn.Exec(ctx,
					template.UpdateUserLocation+`;`,
					lon, lat, id)
				return err
			}, func() error {
				if !cmdTag.Update() {
					return common.ErrUpdateFailed
				}
				return nil
			})

		logger.Trace("update location for user %v", id)
		return nil
	}
}
