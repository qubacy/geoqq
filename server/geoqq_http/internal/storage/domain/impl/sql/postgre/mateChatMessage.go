package postgre

import (
	"context"
	"geoqq_http/internal/domain"
	utl "geoqq_http/pkg/utility"
	"sort"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type MateChatMessageStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateChatMessageStorage(pool *pgxpool.Pool) *MateChatMessageStorage {
	return &MateChatMessageStorage{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	templateInsertMateChatMessageWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "MateMessage" (
			"MateChatId", "FromUserId",
			"Text", "Time", "Read"
		)
		VALUES ($1, $2, $3, NOW()::timestamp, FALSE)`)

	templateInsertMateChatMessage = `` +
		templateInsertMateChatMessageWithoutReturningId +
		` RETURNING "Id"`

	/*
		Order:
			1. userId
			2. chatId
			3. count
			4. offset
	*/
	templateGetMateChatMessagesByChatId = utl.RemoveAdjacentWs(`
		SELECT 
			"MateMessage"."Id" AS "Id",
			"Text",
			"Time",
			"FromUserId" AS "UserId",
			"Read"
		FROM "MateMessage"
		INNER JOIN "MateChat" ON (
			"MateChat"."Id" = "MateMessage"."MateChatId" 
			AND "MateMessage"."MateChatId" = $2
			AND ( 
				"FirstUserId" = $1 OR
				"SecondUserId" = $1
			)
		)
		ORDER BY "Time" DESC
		LIMIT $3 OFFSET $4`)

	/*
		Order:
			1. userId
			2. chatId
			3. count
			4. offset
	*/
	templateReadMateChatMessagesByChatId = utl.RemoveAdjacentWs(`
		WITH "MateChatMessages" AS
			(SELECT 
				"MateMessage"."Id" AS "Id",
				"Text",
				"Time",
				"FromUserId" AS "UserId",
				"Read" /* will return value before update */
			FROM "MateMessage"
			INNER JOIN "MateChat" ON (
				"MateChat"."Id" = "MateMessage"."MateChatId" 
				AND "MateMessage"."MateChatId" = $2
				AND ( 
					"FirstUserId" = $1 OR 
					"SecondUserId" = $1
				) /* access check, without returning obvious errors */
			)
			ORDER BY "Id" DESC /* or time? */
			LIMIT $3 OFFSET $4
			)
		UPDATE "MateMessage" 
			SET "Read" = 
				CASE "MateMessage"."FromUserId"
					WHEN $1 THEN "MateMessage"."Read" /* already set value */
					ELSE TRUE 
				END
		FROM "MateChatMessages"
		WHERE (
			"MateMessage"."Id" = "MateChatMessages"."Id"
		) RETURNING "MateChatMessages".*`)

	/*
		Order:
			1. chatId
	*/
	templateDeleteAllMessagesFromMateChat = utl.RemoveAdjacentWs(`
		DELETE FROM "MateMessage"
			WHERE "MateChatId" = $1`)
)

// public
// -----------------------------------------------------------------------

func (s *MateChatMessageStorage) InsertMateChatMessage(ctx context.Context,
	chatTd, fromUserId uint64, text string) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChatMessage, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertMateChatMessage+`;`,
		chatTd, fromUserId, text)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChatMessage, err)
	}

	return lastInsertedId, nil
}

// -----------------------------------------------------------------------

func (s *MateChatMessageStorage) GetMateChatMessagesByChatId(ctx context.Context,
	userId, chatId uint64, count, offset uint64) (domain.MateMessageList, error) {

	/*
		Who get the messages here is not important!
	*/

	mateMessages, err := s.queryMateChatMessagesByChatId(
		ctx, templateGetMateChatMessagesByChatId,
		userId, chatId, count, offset,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatMessagesByChatId, err)
	}

	return mateMessages, nil
}

// TODO: check count!!! on max size!!

func (s *MateChatMessageStorage) ReadMateChatMessagesByChatId(ctx context.Context,
	userId, chatId uint64, count, offset uint64) (domain.MateMessageList, error) {

	mateMessages, err := s.queryMateChatMessagesByChatId(
		ctx, templateReadMateChatMessagesByChatId,
		userId, chatId, count, offset,
	)

	if err != nil {
		return nil, utl.NewFuncError(s.ReadMateChatMessagesByChatId, err)
	}

	return mateMessages, nil
}

// private
// -----------------------------------------------------------------------

func (s *MateChatMessageStorage) queryMateChatMessagesByChatId(
	ctx context.Context, templateQuery string, // template for read or get!
	userId, chatId, count, offset uint64) (
	domain.MateMessageList, error) {
	sourceFunc := s.queryMateChatMessagesByChatId

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx, templateQuery+`;`,
		userId, chatId, count, offset)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	defer rows.Close()

	// ***

	mateMessages := domain.MateMessageList{}
	for rows.Next() {
		mateMsg, err := mateChatMessageFromRows(rows)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		mateMessages = append(mateMessages, mateMsg)
	}

	// TODO: put sorting in query?
	sort.Slice(mateMessages, func(i, j int) bool {
		return mateMessages[i].Id > mateMessages[j].Id
	})

	return mateMessages, nil
}

func mateChatMessageFromRows(rows pgx.Rows) (*domain.MateMessage, error) {
	mateMessage := new(domain.MateMessage)
	err := rows.Scan(
		&mateMessage.Id,
		&mateMessage.Text,
		&mateMessage.Time,
		&mateMessage.UserId,
		&mateMessage.Read,
	)
	if err != nil {
		return nil, utl.NewFuncError(mateChatMessageFromRows, err)
	}

	return mateMessage, nil
}

// -----------------------------------------------------------------------

func removeAllMessagesFromMateChatInsideTx(ctx context.Context, tx pgx.Tx,
	chatId uint64) error {
	sourceFunc := removeAllMessagesFromMateChatInsideTx

	cmdTag, err := tx.Exec(ctx,
		templateDeleteAllMessagesFromMateChat+`;`,
		chatId,
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	if !cmdTag.Delete() {
		return ErrDeleteFailed
	}

	return nil
}
