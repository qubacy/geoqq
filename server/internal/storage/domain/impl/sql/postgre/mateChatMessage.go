package postgre

import (
	"context"
	"geoqq/internal/domain"
	utl "geoqq/pkg/utility"

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

	templateGetMateChatMessagesByChatId = utl.RemoveAdjacentWs(`
		SELECT 
			"Id",
       		"Text",
       		"Time",
       		"FromUserId" AS "UserId"
		FROM "MateMessage"
		WHERE "MateChatId" = $1
		ORDER BY "Time" DESC
		LIMIT $2 OFFSET $3`)

	templateReadMateChatMessagesByChatId = utl.RemoveAdjacentWs(`
		WITH "MateChatMessages" AS
    		(SELECT 
				"Id",
            	"Text",
            	"Time",
            	"FromUserId" AS "UserId"
     		FROM "MateMessage"
     		WHERE "MateChatId" = $1
     		ORDER BY "Time" DESC
			LIMIT $2 OFFSET $3)
		UPDATE "MateMessage"
		SET "Read" = TRUE
		FROM "MateChatMessages"
		WHERE "MateMessage"."Id" = "MateChatMessages"."Id" 
    		RETURNING "MateChatMessages".*`)
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

func (s *MateChatMessageStorage) GetMateChatMessagesByChatId(ctx context.Context, chatId,
	offset, count uint64) (domain.MateMessageList, error) {

	mateMessages, err := s.queryMateChatMessagesByChatId(
		ctx, templateGetMateChatMessagesByChatId,
		chatId, offset, count,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatMessagesByChatId, err)
	}

	return mateMessages, nil
}

func (s *MateChatMessageStorage) ReadMateChatMessagesByChatId(ctx context.Context, chatId,
	offset, count uint64) (domain.MateMessageList, error) {

	mateMessages, err := s.queryMateChatMessagesByChatId(
		ctx, templateReadMateChatMessagesByChatId,
		chatId, offset, count,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.ReadMateChatMessagesByChatId, err)
	}

	return mateMessages, nil
}

// private
// -----------------------------------------------------------------------

func (s *MateChatMessageStorage) queryMateChatMessagesByChatId(
	ctx context.Context, templateQuery string, chatId, offset, count uint64) (
	domain.MateMessageList, error) {

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.queryMateChatMessagesByChatId, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx, templateQuery+`;`,
		chatId, count, offset)
	if err != nil {
		return nil, utl.NewFuncError(s.queryMateChatMessagesByChatId, err)
	}
	defer rows.Close()

	// ***

	mateMessages := domain.MateMessageList{}
	for rows.Next() {
		mateMsg, err := mateChatMessageFromRows(rows)
		if err != nil {
			return nil, utl.NewFuncError(s.queryMateChatMessagesByChatId, err)
		}

		mateMessages = append(mateMessages, mateMsg)
	}

	return mateMessages, nil
}

func mateChatMessageFromRows(rows pgx.Rows) (*domain.MateMessage, error) {
	mateMessage := new(domain.MateMessage)
	err := rows.Scan(
		&mateMessage.Id, &mateMessage.Text,
		&mateMessage.Time, &mateMessage.UserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(mateChatMessageFromRows, err)
	}

	return mateMessage, nil
}
