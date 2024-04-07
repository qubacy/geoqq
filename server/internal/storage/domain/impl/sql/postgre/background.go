package postgre

import (
	"context"
	"fmt"
	"time"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Background struct {
	*UserStorageBackground

	// ***

	pool    *pgxpool.Pool
	queries chan bgrQueryWrapper

	queryTimeout time.Duration
}

type DependenciesForBgr struct {
	MaxQueryCount int
	QueryTimeout  time.Duration
}

// ctor
// -----------------------------------------------------------------------

func newBackground(
	ctxForInit context.Context,
	ctxWithCancel context.Context,
	pool *pgxpool.Pool,
	deps DependenciesForBgr,

) *Background {
	fmt.Printf("MaxQueryCount: %v\n", deps.MaxQueryCount)
	queries := make(chan bgrQueryWrapper, deps.MaxQueryCount)

	storage := &Background{
		UserStorageBackground: newUserStorageBackground(queries),

		pool:    pool,
		queries: queries,

		queryTimeout: deps.QueryTimeout,
	}

	// can run several...
	go storage.updateQueries(
		ctxForInit,
		ctxWithCancel,
	)

	return storage
}

// private
// -----------------------------------------------------------------------

func (s *Background) updateQueries(
	ctxForInit, ctxWithCancel context.Context, // as field in struct?
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
			fmt.Println("123")
			return

		case f := <-s.queries:
			ctx := context.Background()
			ctx, cancel := context.WithTimeout(
				ctx, s.queryTimeout,
			)
			defer cancel()

			err = f(conn, ctx)
			if err != nil { // only log to file?
				fmt.Println(err)
			}
		}
	}
}
