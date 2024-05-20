package postgre

import (
	utl "common/pkg/utility"
	"context"

	"github.com/jackc/pgconn"
	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

// Scan
// -----------------------------------------------------------------------

type QueryResultScanner interface {
	Scan(dest ...interface{}) error
}

type queryWrapper = func(conn *pgxpool.Conn, ctx context.Context) (pgx.Rows, error)
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

func scanUint64(row QueryResultScanner, sourceFunc any) (uint64, error) {
	var lastInsertedId uint64
	err := row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}
	return lastInsertedId, nil
}

func scanBool(row QueryResultScanner, sourceFunc any) (bool, error) {
	var boolValue bool = false
	err := row.Scan(&boolValue)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	return boolValue, nil
}

// -----------------------------------------------------------------------

func queryWithConnectionAcquire(pool *pgxpool.Pool, ctx context.Context,
	f queryWrapper) (pgx.Rows, error) {

	conn, err := pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(queryWithConnectionAcquire, err)
	}
	defer conn.Release()

	return f(conn, ctx)
}

func scanListOfUint64(rows pgx.Rows, sourceFunc any) ([]uint64, error) {
	numbers := []uint64{}
	for rows.Next() {
		var num uint64
		err := rows.Scan(&num)
		if err != nil {
			// so as not to wrap the error in caller!
			// 								     |
			// 							     -----
			//								 |
			// 								 V
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		numbers = append(numbers, num)
	}
	return numbers, nil
}

// Transaction Wrappers (bad solution)
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

// Transaction Wrappers!
// -----------------------------------------------------------------------

func begunTransaction(pool *pgxpool.Pool, ctx context.Context) (*pgxpool.Conn, pgx.Tx, error) {
	conn, err := pool.Acquire(ctx)
	if err != nil {
		return nil, nil, utl.NewFuncError(begunTransaction, err)
	}

	tx, err := begunTransactionFromConn(conn, ctx)
	if err != nil {
		conn.Release()

		return nil, nil,
			utl.NewFuncError(begunTransaction, err)
	}

	return conn, tx, nil
}

func begunTransactionFromConn(conn *pgxpool.Conn, ctx context.Context) (pgx.Tx, error) {
	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})

	if err != nil {
		return nil, utl.NewFuncError(begunTransactionFromConn, err)
	}
	return tx, err
}

// -----------------------------------------------------------------------

type CommandTagChecker func(cmdTag pgconn.CommandTag) error

func assertCommandTagEqDelete(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Delete() {
		return ErrDeleteFailed
	}
	return nil
}

func assertCommandTagEqInsert(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}
	return nil
}

func assertCommandTagEqUpdate(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}
	return nil
}

// -----------------------------------------------------------------------

func execInsideTx(ctx context.Context, sourceFunc any, check CommandTagChecker,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	cmdTag, err := tx.Exec(ctx,
		templateQueryText+`;`,
		args...,
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	if err := check(cmdTag); err != nil {
		return err
	}
	return nil
}

func deleteInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return execInsideTx(ctx, sourceFunc,
		assertCommandTagEqDelete, tx,
		templateQueryText, args...,
	)
}

func insertInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return execInsideTx(ctx, sourceFunc,
		assertCommandTagEqInsert, tx,
		templateQueryText, args...,
	)
}

func updateInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return execInsideTx(ctx, sourceFunc,
		assertCommandTagEqUpdate, tx,
		templateQueryText, args...,
	)
}
