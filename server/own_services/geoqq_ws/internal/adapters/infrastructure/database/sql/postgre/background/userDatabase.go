package background

import (
	"common/pkg/logger"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/common"
	"math/rand"

	"github.com/jackc/pgconn"
	"github.com/jackc/pgx/v4/pgxpool"
)

type UserDatabase struct {
	pool       *pgxpool.Pool
	queriesChs []QueriesChan
}

func newUserDatabase(pool *pgxpool.Pool, chs []QueriesChan) *UserDatabase {
	return &UserDatabase{
		pool:       pool,
		queriesChs: chs,
	}
}

// public
// -----------------------------------------------------------------------

func (u *UserDatabase) UpdateBgrLastActionTimeForUser(id uint64) {
	sourceFunc := u.UpdateBgrLastActionTimeForUser
	chIndex := rand.Intn(len(u.queriesChs))
	u.queriesChs[chIndex] <- func(conn *pgxpool.Conn, ctx context.Context) error {
		var (
			cmdTag pgconn.CommandTag
			err    error = nil
		)
		err = utl.RunFuncsRetErr(
			func() error {
				cmdTag, err = conn.Exec(ctx,
					template.UpdateLastActionTimeForUser+`;`, id)
				return err
			}, func() error {
				if !cmdTag.Update() {
					return common.ErrUpdateFailed
				}
				return nil
			})
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if logger.Initialized() {
			logger.Trace("update last action time for user %v", id)
		}
		return nil
	}
}

func (u *UserDatabase) UpdateBgrLocationForUser(id uint64, lon, lat float64) {
	sourceFunc := u.UpdateBgrLocationForUser
	chIndex := rand.Intn(len(u.queriesChs))
	u.queriesChs[chIndex] <- func(conn *pgxpool.Conn, ctx context.Context) error {
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
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if logger.Initialized() {
			logger.Trace("update location for user %v", id)
		}
		return nil
	}
}
