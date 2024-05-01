package postgre

import (
	"context"
	"errors"
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"
	"math/rand"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserStorageBackground struct {
	channels []chan bgrQueryWrapper
}

// private ctor
// -----------------------------------------------------------------------

func newUserStorageBackground(
	channelWithQueries []chan bgrQueryWrapper,
) *UserStorageBackground {

	return &UserStorageBackground{
		channels: channelWithQueries,
	}
}

// -----------------------------------------------------------------------

func (s *UserStorageBackground) randomChannelIndex() int {
	return rand.Intn(len(s.channels))
}

func (s *UserStorageBackground) UpdateBgrLastActionTimeForUser(id uint64) {
	sourceFunc := s.UpdateBgrLastActionTimeForUser
	s.channels[s.randomChannelIndex()] <- func(conn *pgxpool.Conn, ctx context.Context) error {
		cmdTag, err := conn.Exec(ctx,
			templateUpdateLastActionTimeForUser+`;`, id,
		)

		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}
		if !cmdTag.Update() {
			return ErrUpdateFailed
		}

		logger.Trace("update last action time for user %v", id)
		return nil
	}
}

func (s *UserStorageBackground) DeleteBgrMateChatsForUser(id uint64) {
	sourceFunc := s.DeleteBgrMateChatsForUser
	s.channels[s.randomChannelIndex()] <- func(conn *pgxpool.Conn, ctx context.Context) error {

		// TODO: if something goes wrong?

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

			firstUserId := id
			err = stepsToDeleteMateChatForUserInsideTx(ctx, tx,
				!mateChat.DeletedForSecond, mateChat.Id,
				firstUserId, mateChat.SecondUserId,
			)

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
