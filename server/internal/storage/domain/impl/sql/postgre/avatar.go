package postgre

import (
	"context"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type AvatarStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newAvatarStorage(pool *pgxpool.Pool) *AvatarStorage {
	return &AvatarStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (self *AvatarStorage) InsertGeneratedAvatar(
	ctx context.Context, hashValue string) (
	uint64, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertGeneratedAvatar, err)
	}
	defer conn.Release()

	var lastInsertedId uint64
	row := conn.QueryRow(ctx,
		`INSERT INTO "Avatar" (
			"GeneratedByServer", "Time", "Hash"
		)
		VALUES (
			TRUE, NOW()::timestamp, $1
		) RETURNING "Id";`,
		hashValue,
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertGeneratedAvatar, err)
	}

	return lastInsertedId, nil
}
