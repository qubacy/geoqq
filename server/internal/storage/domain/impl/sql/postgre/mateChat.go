package postgre

import (
	"context"

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

func (s *MateChatStorage) InsertMateChat(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (uint64, error) {
	return 0, ErrNotImplemented
}
