package postgre

import (
	"context"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type MateChatStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateChatStorage(pool *pgxpool.Pool) *MateChatStorage {
	return &MateChatStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

const (
	templateInsertMateChatWithoutReturningId = `
		INSERT INTO "MateChat" (
			"FirstUserId", 
			"SecondUserId"
		)
		VALUES($1, $2)`
	templateInsertMateChat = templateInsertMateChatWithoutReturningId +
		` RETURNING "Id"`

	templateInsertMateChatMessageWithoutReturningId = `
		INSERT INTO "MateMessage" (
			"MateChatId", "FromUserId",
			"Text", "Time", "Read"
		)
		VALUES ($1, $2, $3, NOW()::timestamp, FALSE)`
	templateInsertMateChatMessage = `` +
		templateInsertMateChatMessageWithoutReturningId +
		` RETURNING "Id"`

	templateHasMateChatWithId = `` +
		`SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Has"
		FROM "MateChat"
		WHERE "Id" = $1`

	templateAvailableMateChatWithIdForUser = `` +
		`SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Available"
		FROM "MateChat"
		WHERE "Id" = $1
			AND ("FirstUserId" = $2
  				OR "SecondUserId" = $2)
			AND NOT EXISTS
 				(SELECT
  				FROM "DeletedMateChat"
  				WHERE "ChatId" = "Id"
	  				AND "UserId" = $2)`
)

// -----------------------------------------------------------------------

func (s *MateChatStorage) InsertMateChat(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertMateChat+`;`,
		firstUserId, secondUserId)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
	}

	return lastInsertedId, nil
}

func (s *MateChatStorage) AvailableMateChatWithIdForUser(ctx context.Context,
	chatId, userId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.AvailableMateChatWithIdForUser, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateAvailableMateChatWithIdForUser+`;`,
		chatId, userId)

	var available bool = false
	err = row.Scan(&available)
	if err != nil {
		return false, utl.NewFuncError(s.AvailableMateChatWithIdForUser, err)
	}

	return available, nil
}

func (s *MateChatStorage) HasMateChatWithId(ctx context.Context, id uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateChatWithId, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx,
		templateHasMateChatWithId+`;`, id)

	var exists bool = false
	err = row.Scan(&exists)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateChatWithId, err)
	}

	return exists, nil
}

func (s *MateChatStorage) InsertMateChatMessage(ctx context.Context,
	chatTd, fromUserId uint64, text string) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChatMessage, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertMateChatMessage+`;`,
		chatTd, fromUserId, text)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChatMessage, err)
	}

	return lastInsertedId, nil
}

// private
// -----------------------------------------------------------------------

func insertMateChatWithoutReturningId(ctx context.Context, tx pgx.Tx,
	firstUserId uint64, secondUserId uint64) error {

	err := insertForUserPairWithoutReturningId(ctx, tx,
		templateInsertMateChatWithoutReturningId+`;`,
		firstUserId, secondUserId,
	)
	if err != nil {
		return utl.NewFuncError(insertMateChatWithoutReturningId, err)
	}

	return nil
}
