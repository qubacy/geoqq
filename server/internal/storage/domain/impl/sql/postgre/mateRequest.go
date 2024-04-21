package postgre

import (
	"context"
	"errors"
	"geoqq/internal/domain/table"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
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

var (
	templateUpdateMateRequestResultById = utl.RemoveAdjacentWs(`
		UPDATE "MateRequest" SET "Result" = $1,
			   "ResponseTime" = NOW()::timestamp
			WHERE "Id" = $2`)

	templateDeleteMateRequestsForUser = utl.RemoveAdjacentWs(`
		DELETE FROM "MateRequest" WHERE (
			"FromUserId" = $1 OR "ToUserId" = $1) AND
    			"Result" = $2`)
)

// -----------------------------------------------------------------------

func (s *MateRequestStorage) AddMateRequest(ctx context.Context,
	fromUserId, toUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.AddMateRequest, err)
	}
	defer conn.Release()

	var lastInsertedId uint64
	row := conn.QueryRow(ctx,
		`INSERT INTO "MateRequest" (
			"FromUserId", "ToUserId",
			"RequestTime", "Result"
		)
		VALUES ($1, $2, NOW()::timestamp, $3) RETURNING "Id";`,
		fromUserId, toUserId, int16(table.Waiting),
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.AddMateRequest, err)
	}

	return lastInsertedId, nil
}

func (s *MateRequestStorage) HasWaitingMateRequest(ctx context.Context,
	fromUserId, toUserId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasWaitingMateRequest, err)
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
		return false, utl.NewFuncError(s.HasWaitingMateRequest, err)
	}

	return count >= 1, nil
}

func (s *MateRequestStorage) IsMateRequestForUser(ctx context.Context, id, userId uint64) (
	bool, error,
) {
	return s.HasMateRequestByIdAndToUser(
		ctx, id, userId)
}

func (s *MateRequestStorage) HasMateRequestByIdAndToUser(ctx context.Context, id, toUserId uint64) (
	bool, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateRequestByIdAndToUser, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "MateRequest" 
			WHERE "ToUserId" = $1 AND "Id" = $2;`, toUserId, id,
	)

	var count int = 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateRequestByIdAndToUser, err)
	}

	return count == 1, nil
}

func (s *MateRequestStorage) GetMateRequestById(ctx context.Context, id uint64) (
	*table.MateRequest, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateRequestById, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT * FROM "MateRequest"
			WHERE "Id" = $1;`, id,
	)

	mateRequest := table.NewMateRequest()
	err = row.Scan(&mateRequest.Id,
		&mateRequest.FromUserId, &mateRequest.ToUserId,
		&mateRequest.RequestTime, &mateRequest.ResponseTime,
		&mateRequest.Result,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateRequestById, err)
	}

	return mateRequest, nil
}

func (s *MateRequestStorage) GetMateRequestResultById(ctx context.Context, id uint64) (
	table.MateRequestResult, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.GetMateRequestResultById, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Result" FROM "MateRequest" WHERE "Id" = $1;`, id,
	)
	var resultNumber int16 = 0
	err = row.Scan(&resultNumber)
	if err != nil {
		return 0, utl.NewFuncError(s.GetMateRequestResultById, err)
	}

	result, err := table.MakeMateResultFromInt(resultNumber)
	if err != nil {
		return 0, utl.NewFuncError(s.GetMateRequestResultById, err) // impossible!
	}

	return result, nil
}

func (s *MateRequestStorage) UpdateMateRequestResultById(ctx context.Context, id uint64,
	value table.MateRequestResult) error {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(s.UpdateMateRequestResultById, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		templateUpdateMateRequestResultById+`;`,
		int16(value), // <--- smallint!
		id,
	)
	if err != nil {
		return utl.NewFuncError(s.UpdateMateRequestResultById, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func (s *MateRequestStorage) AcceptMateRequestById(ctx context.Context,
	id, firstUserId, secondUserId uint64) error {

	/*
		Action List:
			1. Remove records from `DeletedMateChat` for users.
			2. Insert mate (can exist?).
			3. Insert mate chat (if not exists).
			4. Update mate request result.
	*/

	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return utl.NewFuncError(s.AcceptMateRequestById, err)
	}
	defer conn.Release()

	// ***

	err = errors.Join(
		removeDeletedMateChatsForUsersInsideTx(ctx, tx, firstUserId, secondUserId),
		insertMateWithoutReturningIdInsideTx(ctx, tx, firstUserId, secondUserId),
		insertMateChatWithoutReturningIdInsideTx(ctx, tx, firstUserId, secondUserId),
		updateMateRequestResultById(ctx, tx, id, table.Accepted),
	)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx)) // <--- ignore error?
		return utl.NewFuncError(s.AcceptMateRequestById, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(s.AcceptMateRequestById, err)
	}
	return nil
}

func (s *MateRequestStorage) RejectMateRequestById(ctx context.Context,
	id, firstUserId, secondUserId uint64) error {

	/*
		Action List:
			1. Update mate request result.
	*/

	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return utl.NewFuncError(s.RejectMateRequestById, err)
	}
	defer conn.Release()

	err = updateMateRequestResultById(ctx, tx, id, table.Rejected)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx))
		return utl.NewFuncError(s.RejectMateRequestById, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(s.RejectMateRequestById, err)
	}
	return nil
}

// -----------------------------------------------------------------------

func (s *MateRequestStorage) GetAllWaitingMateRequestsForUser(ctx context.Context, userId uint64) (
	[]*table.MateRequest, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetAllWaitingMateRequestsForUser, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx,
		`SELECT * FROM "MateRequest"
			WHERE "ToUserId" = $1 AND "Result" = $2
			ORDER BY "RequestTime" DESC, "Id" DESC;`,
		userId, int16(table.Waiting),
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetAllWaitingMateRequestsForUser, err)
	}

	// ***

	mateRequests, err := rowsToMateRequests(rows)
	if err != nil {
		return nil,
			utl.NewFuncError(s.GetAllWaitingMateRequestsForUser, err)
	}
	return mateRequests, nil
}

func (s *MateRequestStorage) GetWaitingMateRequestsForUser(ctx context.Context,
	userId, offset, count uint64) ([]*table.MateRequest, error) {
	sourceFunc := s.GetWaitingMateRequestsForUser
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx,
		`SELECT * FROM "MateRequest"
		WHERE "ToUserId" = $1 AND "Result" = $2
			ORDER BY "RequestTime" DESC, "Id" DESC 
			LIMIT $3 OFFSET $4;`,
		userId, int16(table.Waiting),
		count, offset,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	// ***

	mateRequests, err := rowsToMateRequests(rows)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateRequests, nil
}

func (s *MateRequestStorage) GetWaitingMateRequestCountForUser(ctx context.Context, userId uint64) (
	int, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.GetWaitingMateRequestCountForUser, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "MateRequest"
			WHERE "ToUserId" = $1 AND "Result" = $2;`,
		userId, int16(table.Waiting),
	)

	var count = 0
	err = row.Scan(&count)
	if err != nil {
		return 0, utl.NewFuncError(s.GetWaitingMateRequestCountForUser, err)
	}

	return count, nil
}

// private
// -----------------------------------------------------------------------

func rowsToMateRequests(rows pgx.Rows) ([]*table.MateRequest, error) {
	mateRequests := []*table.MateRequest{}
	for rows.Next() {
		mateRequest := table.NewMateRequest()
		err := rows.Scan(
			&mateRequest.Id,
			&mateRequest.FromUserId, &mateRequest.ToUserId,
			&mateRequest.RequestTime, &mateRequest.ResponseTime,
			&mateRequest.Result,
		)
		if err != nil {
			return nil,
				utl.NewFuncError(rowsToMateRequests, err)
		}

		mateRequests = append(mateRequests, mateRequest)
	}

	return mateRequests, nil
}

func updateMateRequestResultById(ctx context.Context, tx pgx.Tx,
	id uint64, value table.MateRequestResult) error {

	cmdTag, err := tx.Exec(ctx,
		templateUpdateMateRequestResultById+`;`,
		int16(value),
		id,
	)
	if err != nil {
		return utl.NewFuncError(updateMateRequestResultById, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
