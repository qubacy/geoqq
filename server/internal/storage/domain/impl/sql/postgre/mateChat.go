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
)

func (s *MateChatStorage) InsertMateChat(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertMateChat,
		firstUserId, secondUserId)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
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
