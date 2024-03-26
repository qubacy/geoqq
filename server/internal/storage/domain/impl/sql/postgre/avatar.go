package postgre

import (
	"context"
	"fmt"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
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

// templates
// -----------------------------------------------------------------------

var (
	templateDeleteAvatarWithId = utl.RemoveAdjacentWs(`
		DELETE FROM "Avatar"
			WHERE "Id" = $1`)

	templateInsertSvrGeneratedAvatar = utl.RemoveAdjacentWs(`
		INSERT INTO "Avatar" (
			"UserId", 
			"Time",
			"Hash"
		)
		VALUES (
			NULL, NOW()::timestamp, $1
		) RETURNING "Id"`)

	templateInsertSvrGeneratedAvatarWithLbl = utl.RemoveAdjacentWs(`
		INSERT INTO "Avatar" ( 
			"UserId",
			"Label",
    		"Time", 
			"Hash"
		)
		VALUES (
			NULL, $1, NOW()::timestamp, $2
		) RETURNING "Id"`)

	templateInsertAvatar = utl.RemoveAdjacentWs(`
		INSERT INTO "Avatar" ( 
			"UserId",
			"Time",
			"Hash"
		)
		VALUES (
			$1, NOW()::timestamp, $2
		) RETURNING "Id"`)
)

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

// work with generated avatar
// -----------------------------------------------------------------------

func (s *AvatarStorage) InsertServerGeneratedAvatar(
	ctx context.Context, hashValue string) (
	uint64, error,
) {
	sourceFunc := s.InsertServerGeneratedAvatar

	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn) pgx.Row {
			return conn.QueryRow(ctx,
				templateInsertSvrGeneratedAvatar+`;`,
				hashValue,
			)
		},
	)

	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return scanLastInsertedId(row, sourceFunc)
}

func (s *AvatarStorage) InsertServerGeneratedAvatarWithLabel(ctx context.Context,
	hashValue, label string) (uint64, error) {
	sourceFunc := s.InsertServerGeneratedAvatarWithLabel

	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn) pgx.Row {
			return conn.QueryRow(ctx,
				templateInsertSvrGeneratedAvatarWithLbl+`;`,
				label, hashValue,
			)
		},
	)

	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return scanLastInsertedId(row, sourceFunc)
}

// -----------------------------------------------------------------------

func (s *AvatarStorage) InsertAvatar(
	ctx context.Context, userId uint64, hashValue string) (
	uint64, error,
) {
	sourceFunc := s.InsertAvatar

	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn) pgx.Row {
			return conn.QueryRow(ctx,
				templateInsertAvatar+`;`,
				userId, hashValue,
			)
		},
	)

	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return scanLastInsertedId(row, s.InsertAvatar)
}

// -----------------------------------------------------------------------

func (s *AvatarStorage) DeleteAvatarWithId(
	ctx context.Context, id uint64) error {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(s.DeleteAvatarWithId, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		templateDeleteAvatarWithId+`;`, id)
	if err != nil {
		return utl.NewFuncError(s.DeleteAvatarWithId, err)
	}
	if !cmdTag.Delete() {
		return ErrDeleteFailed
	}

	return nil
}
