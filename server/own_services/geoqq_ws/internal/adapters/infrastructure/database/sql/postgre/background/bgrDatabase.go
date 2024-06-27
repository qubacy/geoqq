package background // internal

import (
	"common/pkg/logger"
	"common/pkg/postgreUtils/wrappedPgxpool"
	utl "common/pkg/utility"
	"context"
	"time"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Params struct {
	MaxWorkerCount int
	MaxQueryCount  int
	QueryTimeout   time.Duration
}

type QueriesChan = chan wrappedPgxpool.BgrQueryWrapper

type BgrDatabase struct {
	*UserDatabase // private fields are not used!

	queriesChs   []QueriesChan
	queryTimeout time.Duration

	cancelWorkers context.CancelFunc
	workerCtx     context.Context
	workerConns   []*pgxpool.Conn
}

func New(startCtx context.Context, pool *pgxpool.Pool, params Params) (*BgrDatabase, error) {
	chCount := params.MaxWorkerCount
	chs := make([]QueriesChan, 0, chCount) // with cap!
	for i := 0; i < chCount; i++ {
		chs = append(chs, make(QueriesChan, params.MaxQueryCount))
	}

	bgrDb := &BgrDatabase{
		UserDatabase: newUserDatabase(pool, chs),
		queriesChs:   chs,
		queryTimeout: params.QueryTimeout,
	}
	bgrDb.workerCtx, bgrDb.cancelWorkers =
		context.WithCancel(context.Background())

	// ***

	bgrDb.workerConns = make([]*pgxpool.Conn, 0, len(chs))
	for i := 0; i < len(chs); i++ {
		if conn, err := pool.Acquire(startCtx); err != nil { // check limits?
			bgrDb.Stop(startCtx)
			return nil, utl.NewFuncError(New, err)
		} else {
			bgrDb.workerConns = append(bgrDb.workerConns, conn)
		}
	}

	for i := 0; i < len(chs); i++ {
		go bgrDb.runWorker(i, chs[i], bgrDb.workerConns[i])
	}

	return bgrDb, nil
}

func (b *BgrDatabase) runWorker(idx int, ch QueriesChan, conn *pgxpool.Conn) {
	for {
		select {
		case <-b.workerCtx.Done():
			if logger.Initialized() {
				logger.Info("background database worker #%v stopped", idx)
			}
			return

		case executeQuery := <-ch:
			ctx := context.Background()
			ctx, cancel := context.WithTimeout(ctx, b.queryTimeout)

			if err := executeQuery(conn, ctx); err != nil {
				if logger.Initialized() {
					logger.Error("%v", utl.NewFuncError(b.runWorker, err))
				}
			}
			cancel()
		}
	}
}

// public
// -----------------------------------------------------------------------

func (b *BgrDatabase) Stop(ctx context.Context) error {
	b.cancelWorkers()

	if b.queriesChs != nil {
		for i := range b.queriesChs {
			close(b.queriesChs[i])
		}
	}
	for b.workerConns != nil {
		for i := range b.workerConns {
			b.workerConns[i].Release()
		}
	}
	return nil
}
