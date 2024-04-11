package postgre

import (
	"context"
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserStorageBackground struct {
	queries chan<- bgrQueryWrapper
}

// private ctor
// -----------------------------------------------------------------------

func newUserStorageBackground(
	queries chan<- bgrQueryWrapper) *UserStorageBackground {

	return &UserStorageBackground{
		queries: queries,
	}

}

// -----------------------------------------------------------------------

func (s *UserStorageBackground) UpdateBgrLastActivityTimeForUser(id uint64) {
	logger.Trace("try add task")
	s.queries <- func(conn *pgxpool.Conn, ctx context.Context) error {
		cmdTag, err := conn.Exec(ctx,
			templateUpdateLastActivityTimeForUser+`;`, id,
		)

		if err != nil {
			return utl.NewFuncError(s.UpdateBgrLastActivityTimeForUser, err)
		}
		if !cmdTag.Update() {
			return ErrUpdateFailed
		}

		return nil
	}
	logger.Trace("add task")
}
