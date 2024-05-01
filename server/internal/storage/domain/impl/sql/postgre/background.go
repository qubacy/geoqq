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
}

type DependenciesForBgr struct {
	MaxWorkerCount int
	MaxQueryCount  int
	QueryTimeout   time.Duration
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
	logger.Trace("max worker count: %v", deps.MaxWorkerCount)
	workerCount := deps.MaxWorkerCount

	// ***

	capturedConns := make([]*pgxpool.Conn, 0, workerCount)
	for i := 0; i < workerCount; i++ {
		conn, err := pool.Acquire(ctxForInit)
		if err != nil {
			return nil, utility.NewFuncError(
				newBackground, err)
		}

		capturedConns = append(capturedConns, conn)
	}
	logger.Info("background storage has captured [%v] conns",
		workerCount)

	// ***

	workers := make([]*backgroundWorker, 0, workerCount)
	channelWithQueries := make([]chan bgrQueryWrapper, 0, workerCount)

	for i := 0; i < workerCount; i++ {
		queries := make(chan bgrQueryWrapper, deps.MaxQueryCount)
		channelWithQueries = append(channelWithQueries, queries)

		worker := &backgroundWorker{
			ctxWithCancel: ctxWithCancel,
			queryTimeout:  deps.QueryTimeout,
			queries:       queries,
		}
		workers = append(workers, worker)
	}
	for i := range workers {
		go workers[i].exec(capturedConns[i])
	}

	storage := &Background{
		UserStorageBackground: newUserStorageBackground(
			channelWithQueries),
	}
	return storage, nil
}

// worker
// -----------------------------------------------------------------------

type backgroundWorker struct {
	ctxWithCancel context.Context
	queries       chan bgrQueryWrapper
	queryTimeout  time.Duration
}

func (w *backgroundWorker) exec(openedConn *pgxpool.Conn) {
	defer openedConn.Release()

	// ***

	for {
		select {
		case <-w.ctxWithCancel.Done(): // ?
			close(w.queries)

			logger.Warning("queries canceled")
			return

		case f, ok := <-w.queries:
			if !ok {
				logger.Warning("queries channel closed")
				return
			}

			ctx := context.Background()
			ctx, cancel := context.WithTimeout(
				ctx, w.queryTimeout,
			)
			defer cancel()

			err := f(openedConn, ctx)
			if err != nil {
				logger.Error("query with err: %v", err)
			}
		}
	}
}
