package postgre

import (
	"context"
	"fmt"
	utl "geoqq/pkg/utility"

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

func (s *AvatarStorage) HasAvatar(ctx context.Context, id uint64) (
	bool, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasAvatar, err)
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
		return false, utl.NewFuncError(s.HasAvatar, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}

	return count == 1, nil
}

func (s *AvatarStorage) HasAvatars(ctx context.Context, uniqueIds []uint64) (
	bool, error,
) {
	if len(uniqueIds) == 0 {
		return true, nil
	}

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasAvatars, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		fmt.Sprintf(
			`SELECT COUNT(*) AS "Count"
				FROM "Avatar" WHERE "Id" IN (%v);`,
			utl.NumbersToString(uniqueIds),
		),
	)

	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(s.HasAvatars, err)
	}

	return count == len(uniqueIds), nil
}

func (s *AvatarStorage) InsertGeneratedAvatar(
	ctx context.Context, hashValue string) (
	uint64, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeneratedAvatar, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`INSERT INTO "Avatar" (
			"GeneratedByServer", "Time", "Hash"
		)
		VALUES (
			TRUE, NOW()::timestamp, $1
		) RETURNING "Id";`,
		hashValue,
	)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeneratedAvatar, err)
	}

	return lastInsertedId, nil
}

func (s *AvatarStorage) InsertAvatar(
	ctx context.Context, hashValue string) (
	uint64, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertAvatar, err)
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
		return 0, utl.NewFuncError(s.InsertAvatar, err)
	}

	return lastInsertedId, nil
}
