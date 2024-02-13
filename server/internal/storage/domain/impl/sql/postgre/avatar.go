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

func (self *AvatarStorage) HasAvatar(ctx context.Context, id uint64) (
	bool, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(self.HasAvatar, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) 
		FROM "Avatar" WHERE "Id" = $1;`,
		id,
	)

	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.NewFuncError(self.HasAvatar, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}

	return count == 1, nil
}

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

func (self *AvatarStorage) InsertAvatar(
	ctx context.Context, hashValue string) (
	uint64, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertAvatar, err)
	}
	defer conn.Release()

	var lastInsertedId uint64
	row := conn.QueryRow(ctx,
		`INSERT INTO "Avatar" (
			"GeneratedByServer", "Time", "Hash"
		)
		VALUES (
			FALSE, NOW()::timestamp, $1
		) RETURNING "Id";`,
		hashValue,
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertAvatar, err)
	}

	return lastInsertedId, nil
}
