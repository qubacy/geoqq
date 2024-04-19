package postgre

import (
	"context"
	"errors"
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
	sourceFunc := s.UpdateBgrLastActivityTimeForUser
	s.queries <- func(conn *pgxpool.Conn, ctx context.Context) error {
		cmdTag, err := conn.Exec(ctx,
			templateUpdateLastActivityTimeForUser+`;`, id,
		)

		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}
		if !cmdTag.Update() {
			return ErrUpdateFailed
		}

		return nil
	}
}

// TODO: если что-то пойдет не так?
func (s *UserStorageBackground) DeleteBgrMateChatsForUser(id uint64) {
	sourceFunc := s.DeleteBgrMateChatsForUser
	s.queries <- func(conn *pgxpool.Conn, ctx context.Context) error {
		mateChats, err := getAvailableMateChatsForFirstUser(conn, ctx, id)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		// ***

		for _, mateChat := range mateChats {
			logger.Trace("delete mate chat %v", *mateChat)
			tx, err := begunTransactionFromConn(conn, ctx)
			if err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}

			err = stepsToDeleteMateChatForUserInsideTx(ctx, tx,
				!mateChat.DeletedForSecond,
				mateChat.Id, id, mateChat.SecondUserId)

			if err != nil {
				err = errors.Join(err, tx.Rollback(ctx)) // ?
				return utl.NewFuncError(sourceFunc, err)
			}

			if err := tx.Commit(ctx); err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}
		}
		return nil
	}
}
