package background // internal

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
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
	*UserDatabase
}

func New(pool *pgxpool.Pool, params Params) (*BgrDatabase, error) {
	chCount := params.MaxWorkerCount
	chs := make([]QueriesChan, 0, chCount) // with cap!
	for i := 0; i < chCount; i++ {
		chs = append(chs, make(QueriesChan, params.MaxQueryCount))
	}

	return &BgrDatabase{
		UserDatabase: newUserDatabase(pool, chs),
	}, nil
}

// exec
// -----------------------------------------------------------------------
