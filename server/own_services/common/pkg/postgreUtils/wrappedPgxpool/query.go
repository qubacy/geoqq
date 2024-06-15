package wrappedPgxpool

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

type QueryWrapper = func(conn *pgxpool.Conn, ctx context.Context) (pgx.Rows, error)
type RowQueryWrapper = func(conn *pgxpool.Conn, ctx context.Context) pgx.Row
type BgrQueryWrapper = func(conn *pgxpool.Conn, ctx context.Context) error

func QueryRowWithConnectionAcquire(pool *pgxpool.Pool, ctx context.Context,
	f RowQueryWrapper) (pgx.Row, error) {

	if conn, err := pool.Acquire(ctx); err != nil {
		return nil, utl.NewFuncError(QueryRowWithConnectionAcquire, err)
	} else {

		defer func() { conn.Release() }()
		return f(conn, ctx), nil
	}
}

func ScanUint64(row QueryResultScanner, sourceFunc any) (uint64, error) {
	var lastInsertedId uint64
	if err := row.Scan(&lastInsertedId); err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}
	return lastInsertedId, nil
}

func ScanBool(row QueryResultScanner, sourceFunc any) (bool, error) {
	var boolValue bool = false
	if err := row.Scan(&boolValue); err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	return boolValue, nil
}

// -----------------------------------------------------------------------

func QueryWithConnectionAcquire(pool *pgxpool.Pool, ctx context.Context,
	f QueryWrapper) (pgx.Rows, error) {

	if conn, err := pool.Acquire(ctx); err != nil {
		return nil, utl.NewFuncError(QueryWithConnectionAcquire, err)
	} else {

		defer conn.Release()
		return f(conn, ctx)
	}
}

func ScanListOfUint64(rows pgx.Rows, sourceFunc any) ([]uint64, error) {
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

// Transaction Wrappers!
// -----------------------------------------------------------------------

func BegunTransaction(pool *pgxpool.Pool, ctx context.Context) (*pgxpool.Conn, pgx.Tx, error) {
	sourceFunc := BegunTransaction
	conn, err := pool.Acquire(ctx)
	if err != nil {
		return nil, nil, utl.NewFuncError(sourceFunc, err)
	}

	tx, err := BegunTransactionFromConn(conn, ctx)
	if err != nil {
		conn.Release()

		return nil, nil,
			utl.NewFuncError(sourceFunc, err)
	}

	return conn, tx, nil
}

func BegunTransactionFromConn(conn *pgxpool.Conn, ctx context.Context) (pgx.Tx, error) {
	sourceFunc := BegunTransactionFromConn
	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})

	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return tx, err
}

// Asserts
// -----------------------------------------------------------------------

type CommandTagChecker func(cmdTag pgconn.CommandTag) error

func AssertCommandTagEqDelete(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Delete() {
		return ErrDeleteFailed
	}
	return nil
}

func AssertCommandTagEqInsert(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}
	return nil
}

func AssertCommandTagEqUpdate(cmdTag pgconn.CommandTag) error {
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}
	return nil
}

// -----------------------------------------------------------------------

func ExecInsideTx(ctx context.Context, sourceFunc any, check CommandTagChecker,
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

func DeleteInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return ExecInsideTx(ctx, sourceFunc,
		AssertCommandTagEqDelete, tx,
		templateQueryText, args...,
	)
}

func InsertInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return ExecInsideTx(ctx, sourceFunc,
		AssertCommandTagEqInsert, tx,
		templateQueryText, args...,
	)
}

func UpdateInsideTx(ctx context.Context, sourceFunc any,
	tx pgx.Tx, templateQueryText string, args ...any) error {
	return ExecInsideTx(ctx, sourceFunc,
		AssertCommandTagEqUpdate, tx,
		templateQueryText, args...,
	)
}
