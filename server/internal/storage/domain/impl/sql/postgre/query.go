package postgre

import (
	"context"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

func begunTransaction(pool *pgxpool.Pool, ctx context.Context) (*pgxpool.Conn, pgx.Tx, error) {
	conn, err := pool.Acquire(ctx)
	if err != nil {
		return nil, nil, utl.NewFuncError(begunTransaction, err)
	}

	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})
	if err != nil {
		conn.Release()

		return nil, nil, utl.NewFuncError(begunTransaction, err)
	}

	return conn, tx, nil
}

// -----------------------------------------------------------------------

type rowQueryWrapper = func(conn *pgxpool.Conn, ctx context.Context) pgx.Row
type bgrQueryWrapper = func(conn *pgxpool.Conn, ctx context.Context) error

func queryRowWithConnectionAcquire(pool *pgxpool.Pool, ctx context.Context,
	f rowQueryWrapper) (pgx.Row, error) {

	conn, err := pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(queryRowWithConnectionAcquire, err)
	}
	defer conn.Release()

	return f(conn, ctx), nil
}

func scanLastInsertedId(row pgx.Row, sourceFunc any) (uint64, error) {
	var lastInsertedId uint64
	err := row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return lastInsertedId, nil
}

// -----------------------------------------------------------------------

func insertForUserPairWithoutReturningIdInsideTx(
	ctx context.Context, tx pgx.Tx, templateQueryText string,
	firstUserId, secondUserId uint64) error {

	cmdTag, err := tx.Exec(ctx, templateQueryText+`;`,
		firstUserId, secondUserId,
	)

	if err != nil {
		return utl.NewFuncError(insertForUserPairWithoutReturningIdInsideTx, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func insertGeoChatMessageInsideTx(ctx context.Context, tx pgx.Tx,
	fromUserId uint64, text string,
	latitude, longitude float64) (uint64, error) {

	row := tx.QueryRow(ctx, templateInsertGeoChatMessage+`;`,
		fromUserId, text, latitude, longitude,
	)

	var lastInsertedId uint64
	err := row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(insertGeoChatMessageInsideTx, err)
	}

	return lastInsertedId, nil
}

func updateUserLocationInsideTx(ctx context.Context, tx pgx.Tx, id uint64,
	longitude, latitude float64) error {

	cmdTag, err := tx.Exec(ctx, templateUpdateUserLocation+`;`,
		longitude, latitude, id,
	)

	if err != nil {
		return utl.NewFuncError(updateUserLocationInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
