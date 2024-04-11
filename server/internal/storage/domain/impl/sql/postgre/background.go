package postgre

import (
	"context"
	"fmt"
	"geoqq/pkg/logger"
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
	// TODO: контекст  другой горутине закрываеться!!!
	conn, err := s.pool.Acquire(context.Background())
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(conn)
	defer conn.Release()

	// ***

	for {
		select {
		case <-ctxWithCancel.Done(): // ?
			close(s.queries)
			logger.Warning("update queries canceled")
			return

		// TOOD:!!!!z
		case f, ok := <-s.queries:
			if !ok {
				logger.Warning("ERRRRRRR!")
				return // Выйти из цикла
			}

			ctx := context.Background()
			ctx, cancel := context.WithTimeout(
				ctx, s.queryTimeout,
			)
			defer cancel()

			err = f(conn, ctx)
			logger.Trace("f done")

			if err != nil {
				logger.Error("update query with err: %v", err)
			}
		}
	}
}
