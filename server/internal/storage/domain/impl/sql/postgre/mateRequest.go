package postgre

import (
	"context"
	"geoqq/internal/domain/table"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type MateRequestStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateRequestStorage(pool *pgxpool.Pool) *MateRequestStorage {
	return &MateRequestStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (s *MateRequestStorage) AddMateRequest(ctx context.Context,
	fromUserId, toUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.NewFuncError(s.AddMateRequest, err)
	}
	defer conn.Release()

	var lastInsertedId uint64
	row := conn.QueryRow(ctx,
		`INSERT INTO "MateRequest" (
			"FromUserId",
			"ToUserId",
			"RequestTime",
			"Result")
		VALUES ($1, $2, NOW()::timestamp, $3) RETURNING "Id";`,
		fromUserId, toUserId, int16(table.Waiting),
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.NewFuncError(s.AddMateRequest, err)
	}

	return lastInsertedId, nil
}

func (s *MateRequestStorage) HasWaitingMateRequest(ctx context.Context,
	fromUserId, toUserId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(s.HasWaitingMateRequest, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "MateRequest"
			WHERE "FromUserId" = $1 AND "ToUserId" = $2
				AND "Result" = $3;`,
		fromUserId, toUserId, int16(table.Waiting),
	)

	var count int = 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.NewFuncError(s.HasWaitingMateRequest, err)
	}

	return count >= 1, nil
}

func (s *MateRequestStorage) HasMateRequestByIdAndToUser(ctx context.Context, id, toUserId uint64) (
	bool, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(s.HasMateRequestByIdAndToUser, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "MateRequest" 
			WHERE "ToUserId" = $1 AND "Id" = $2;`, toUserId, id,
	)

	var count int = 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.NewFuncError(s.HasMateRequestByIdAndToUser, err)
	}

	return count == 1, nil
}

func (s *MateRequestStorage) GetMateRequestResultById(ctx context.Context, id uint64) (
	table.MateRequestResult, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.NewFuncError(s.GetMateRequestResultById, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Result" FROM "MateRequest" WHERE "Id" = $1;`, id,
	)
	var resultNumber int16 = 0
	err = row.Scan(&resultNumber)
	if err != nil {
		return 0, utility.NewFuncError(s.GetMateRequestResultById, err)
	}

	result, err := table.MakeMateResultFromInt(resultNumber)
	if err != nil {
		return 0, utility.NewFuncError(s.GetMateRequestResultById, err) // impossible!
	}

	return result, nil
}

func (s *MateRequestStorage) UpdateMateRequestResultById(ctx context.Context, id uint64,
	value table.MateRequestResult) error {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return utility.NewFuncError(s.UpdateMateRequestResultById, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		`UPDATE "MateRequest" SET "Result" = $1 WHERE "Id" = $2;`,
		int16(value), id,
	)
	if err != nil {
		return utility.NewFuncError(s.UpdateMateRequestResultById, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
