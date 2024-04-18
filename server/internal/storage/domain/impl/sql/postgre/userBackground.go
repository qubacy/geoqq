package postgre

import (
	"context"
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

func (s *UserStorageBackground) DeleteBgrMateChatsForUser(id uint64) {
	sourceFunc := s.DeleteBgrMateChatsForUser
	s.queries <- func(conn *pgxpool.Conn, ctx context.Context) error {
		mateChats, err := getAvailableTableMateChatsForUser(conn, ctx, id)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		// ***

		tx, err := begunTransactionFromConn(conn, ctx)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		for _, mateChat := range mateChats {
			//delChatId...

			// see DeleteMateChatForUser!!
		}

		err = tx.Commit(ctx)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		return nil
	}
}
