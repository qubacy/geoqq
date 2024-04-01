package postgre

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Background struct {
	*UserStorageBackground

	// ***

	pool    *pgxpool.Pool
	queries chan bgrQueryWrapper
}

// ctor
// -----------------------------------------------------------------------

func newBackgroundStorage(
	ctxForInit context.Context,
	ctxWithCancel context.Context,
	pool *pgxpool.Pool,
	maxQueryCount int,

) *Background {
	queries := make(chan bgrQueryWrapper, maxQueryCount)

	storage := &Background{
		UserStorageBackground: newUserStorageBackground(queries),

		pool:    pool,
		queries: queries,
	}

	// can run several...
	go storage.updateQueriesInBgr(
		ctxForInit,
		ctxWithCancel,
	)

	return storage
}

// private
// -----------------------------------------------------------------------

func (s *Background) updateQueriesInBgr(
	ctxForInit, ctxWithCancel context.Context,
) {
	conn, err := s.pool.Acquire(ctxForInit)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer conn.Release()

	// ***

	for {
		select {
		case <-ctxWithCancel.Done():
			return

		case f := <-s.queries:
			err = f(conn, context.TODO())
			if err != nil { // only log to file?
				// TODO:
				fmt.Println(err)
			}
		}
	}
}
