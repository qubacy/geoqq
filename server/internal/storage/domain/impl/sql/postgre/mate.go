package postgre

import (
	"context"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type MateStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateStorage(pool *pgxpool.Pool) *MateStorage {
	return &MateStorage{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	templateInsertMateWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "Mate" ("FirstUserId", "SecondUserId")
			VALUES ($1, $2)`)

	templateInsertMate = templateInsertMateWithoutReturningId +
		` RETURNING "Id"`

	/*
		Order:
			1. firstUserId
			2. secondUserId
	*/
	templateDeleteMate = utl.RemoveAdjacentWs(`
		DELETE FROM "Mate" WHERE ("FirstUserId" = $1 OR "SecondUserId" = $1)
				AND ("FirstUserId" = $2 OR "SecondUserId" = $2)`)
)

// public
// -----------------------------------------------------------------------

func (s *MateStorage) AreMates(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.AreMates, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "Mate"
			WHERE ("FirstUserId" = $1 AND "SecondUserId" = $2)
				OR ("FirstUserId" = $2 AND "SecondUserId" = $1);`,
		firstUserId, secondUserId)

	var count = 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(s.AreMates, err)
	}
	if count > 1 { // impossible!
		return false, ErrUnexpectedResult
	}

	return count == 1, nil
}

func (s *MateStorage) InsertMate(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMate, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx, templateInsertMate+`;`,
		firstUserId, secondUserId)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMate, err)
	}

	return lastInsertedId, nil
}

// private
// -----------------------------------------------------------------------

func insertMateWithoutReturningIdInsideTx(ctx context.Context, tx pgx.Tx,
	firstUserId uint64, secondUserId uint64) error {
	sourceFunc := insertMateWithoutReturningIdInsideTx
	err := insertForUserPairWithoutReturningIdInsideTx(ctx, tx,
		templateInsertMateWithoutReturningId,
		firstUserId, secondUserId,
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}

func deleteMateInsideTx(ctx context.Context, tx pgx.Tx,
	firstUserId, secondUserId uint64) error {

	cmdTag, err := tx.Exec(ctx, templateDeleteMate+`;`,
		firstUserId, secondUserId,
	)

	if err != nil {
		return utl.NewFuncError(deleteMateInsideTx, err)
	}
	if !cmdTag.Delete() { // ?
		return ErrDeleteFailed
	}

	return nil
}
