package postgre

import (
	"context"
	"geoqq/internal/domain"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type MateChatStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newMateChatStorage(pool *pgxpool.Pool) *MateChatStorage {
	return &MateChatStorage{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	templateInsertMateChatWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "MateChat" (
			"FirstUserId", 
			"SecondUserId"
		)
		VALUES($1, $2)`)
	templateInsertMateChat = templateInsertMateChatWithoutReturningId +
		` RETURNING "Id"`

	templateHasMateChatWithId = `` +
		`SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Has"
		FROM "MateChat"
		WHERE "Id" = $1`

	templateAvailableMateChatWithIdForUser = utl.RemoveAdjacentWs(`
		SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Available"
		FROM "MateChat"
		WHERE "Id" = $1
			AND ("FirstUserId" = $2
  				OR "SecondUserId" = $2)
			AND NOT EXISTS
 				(SELECT
  				FROM "DeletedMateChat"
  				WHERE "ChatId" = "Id"
	  				AND "UserId" = $2)`)

	templateGetMateChatsForUser = utl.RemoveAdjacentWs(`
		SELECT "MateChat"."Id" AS "Id",
			case
				when "FirstUserId" = $1 
				then "SecondUserId" else "FirstUserId"
			end as "UserId",

			(SELECT COUNT(*) FROM "MateMessage"
			WHERE "MateChatId" = "MateChat"."Id"
				AND "Read" = false) AS "NewMessageCount",

			case
				when "LastMessage"."Id" is NULL 
				then false else true
			end as "Exists",

			"LastMessage"."Id" AS "LastMessageId",
			"LastMessage"."Text" AS "LastMessageText",
			"LastMessage"."Time" AS "LastMessageTime",
			"LastMessage"."FromUserId" AS "LastMessageUserId"

		FROM "MateChat"
		LEFT JOIN LATERAL
 			(SELECT *
  			FROM "MateMessage"
  			WHERE "MateChatId" = "MateChat"."Id"
			ORDER BY "Time" DESC
  			LIMIT 1) "LastMessage" ON true
		WHERE ("FirstUserId" = $1 OR
			   "SecondUserId" = $1)
		ORDER BY "Id" 
		LIMIT $2 OFFSET $3`)
)

// public
// -----------------------------------------------------------------------

func (s *MateChatStorage) InsertMateChat(ctx context.Context,
	firstUserId uint64, secondUserId uint64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertMateChat+`;`,
		firstUserId, secondUserId)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertMateChat, err)
	}

	return lastInsertedId, nil
}

func (s *MateChatStorage) AvailableMateChatWithIdForUser(ctx context.Context,
	chatId, userId uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.AvailableMateChatWithIdForUser, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateAvailableMateChatWithIdForUser+`;`,
		chatId, userId)

	var available bool = false
	err = row.Scan(&available)
	if err != nil {
		return false, utl.NewFuncError(s.AvailableMateChatWithIdForUser, err)
	}

	return available, nil
}

func (s *MateChatStorage) HasMateChatWithId(ctx context.Context, id uint64) (bool, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateChatWithId, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx,
		templateHasMateChatWithId+`;`, id)

	var exists bool = false
	err = row.Scan(&exists)
	if err != nil {
		return false, utl.NewFuncError(s.HasMateChatWithId, err)
	}

	return exists, nil
}

// -----------------------------------------------------------------------

func (s *MateChatStorage) GetMateChatsForUser(ctx context.Context,
	userId, offset, count uint64) ([]*domain.MateChat, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatsForUser, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx, templateGetMateChatsForUser+`;`,
		userId, count, offset)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatsForUser, err)
	}
	defer rows.Close()

	mateChats := []*domain.MateChat{}
	for rows.Next() {
		mateChat, err := mateChatFromRows(rows)
		if err != nil {
			return nil, utl.NewFuncError(
				s.GetMateChatsForUser, err)
		}

		mateChats = append(mateChats, mateChat)
	}

	return mateChats, nil
}

// private
// -----------------------------------------------------------------------

func insertMateChatWithoutReturningId(ctx context.Context, tx pgx.Tx,
	firstUserId uint64, secondUserId uint64) error {

	err := insertForUserPairWithoutReturningId(ctx, tx,
		templateInsertMateChatWithoutReturningId+`;`,
		firstUserId, secondUserId,
	)
	if err != nil {
		return utl.NewFuncError(insertMateChatWithoutReturningId, err)
	}

	return nil
}

// -----------------------------------------------------------------------

func mateChatFromRows(rows pgx.Rows) (*domain.MateChat, error) {
	mateChat := domain.NewEmptyMateChat()
	lastMessageExists := false

	err := rows.Scan(
		&mateChat.Id, &mateChat.UserId,
		&mateChat.NewMessageCount,
		&lastMessageExists,
		nil, nil, nil, nil, // <--- skip fields!
	)
	if err != nil {
		return nil, utl.NewFuncError(mateChatFromRows, err)
	}

	// ***

	var lastMessage *domain.MateMessage = nil
	if lastMessageExists {
		lastMessage, err = lastMateChatMessageFromRows(rows)
		if err != nil {
			return nil, utl.NewFuncError(mateChatFromRows, err)
		}
	}

	mateChat.LastMessage = lastMessage
	return mateChat, nil
}

func lastMateChatMessageFromRows(rows pgx.Rows) (*domain.MateMessage, error) {
	mateMessage := new(domain.MateMessage)
	err := rows.Scan(
		nil, nil, nil, nil,
		&mateMessage.Id, &mateMessage.Text,
		&mateMessage.Time, &mateMessage.UserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(lastMateChatMessageFromRows, err)
	}

	return mateMessage, nil
}
