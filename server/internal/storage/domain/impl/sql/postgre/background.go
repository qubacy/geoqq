package postgre

import (
	"context"
	"geoqq/pkg/logger"
	"geoqq/pkg/utility"
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

) (*Background, error) {
	logger.Trace("max query count: %v", deps.MaxQueryCount)
	queries := make(chan bgrQueryWrapper, deps.MaxQueryCount)

	storage := &Background{
		UserStorageBackground: newUserStorageBackground(queries),

		pool:    pool,
		queries: queries,

		queryTimeout: deps.QueryTimeout,
	}

	conn, err := pool.Acquire(ctxForInit)
	if err != nil {
		return nil, utility.NewFuncError(newBackground, err)
	}
	logger.Info("background storage has captured conn")

	// can run several...
	go storage.updateQueries(
		conn, ctxWithCancel,
	)

	return storage, nil
}

// private
// -----------------------------------------------------------------------

func (s *Background) updateQueries(
	openedConn *pgxpool.Conn,
	ctxWithCancel context.Context, // as field in struct?
) {
	defer openedConn.Release()

	// ***

	for {
		select {
		case <-ctxWithCancel.Done(): // ?
			close(s.queries)

			logger.Warning("update queries canceled")
			return

		case f, ok := <-s.queries:
			if !ok {
				logger.Warning("queries channel closed")
				return
			}

			ctx := context.Background()
			ctx, cancel := context.WithTimeout(
				ctx, s.queryTimeout,
			)
			defer cancel()

			err := f(openedConn, ctx)
			if err != nil {
				logger.Error("update query with err: %v", err)
			}
		}
	}
}
