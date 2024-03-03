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

func insertForUserPairWithoutReturningId(
	ctx context.Context, tx pgx.Tx, queryText string,
	firstUserId, secondUserId uint64) error {

	cmdTag, err := tx.Exec(ctx, queryText,
		firstUserId, secondUserId,
	)

	if err != nil {
		return utl.NewFuncError(insertForUserPairWithoutReturningId, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}
