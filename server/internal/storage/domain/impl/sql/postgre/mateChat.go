package postgre

import (
	"context"
	"errors"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
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

	templateHasMateChatWithId = utl.RemoveAdjacentWs(`
		SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Has"
		FROM "MateChat"
		WHERE "Id" = $1`)

	templateGetMateChatWithIdFromTable = utl.RemoveAdjacentWs(`
		SELECT "Id", "FirstUserId", "SecondUserId" 
			FROM "MateChat" WHERE "Id" = $1`)

	/*
		Order:
			1. chatId
			2. userId
	*/
	templateGetTableMateChatWithIdForUser = utl.RemoveAdjacentWs(`
		SELECT "Id", "FirstUserId", "SecondUserId" 
    		FROM "MateChat" WHERE "Id" = $1 AND (
				"FirstUserId" = $2 OR "SecondUserId" = $2)`)

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

	/*
		Order:
			1. userId
	*/
	templateAllGetMateChatsForUser = utl.RemoveAdjacentWs(`
		SELECT "MateChat"."Id" AS "Id",
			case
				when "FirstUserId" = $1 
				then "SecondUserId" else "FirstUserId"
			end as "UserId",

			(SELECT COUNT(*) FROM "MateMessage"
			WHERE "MateChatId" = "MateChat"."Id"
				AND "MateMessage"."FromUserId" != $1 
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
		LEFT JOIN "DeletedMateChat" ON 
    		("DeletedMateChat"."ChatId" = "MateChat"."Id"
				AND "DeletedMateChat"."UserId" = $1)
		WHERE ("FirstUserId" = $1 OR "SecondUserId" = $1) AND 
			"DeletedMateChat"."ChatId" IS NULL`)

	/*
		Order:
			1. userId
			2. count
			3. offset
	*/
	templateGetMateChatsForUser = templateAllGetMateChatsForUser +
		` ORDER BY "Id" LIMIT $2 OFFSET $3`

	/*
		Order:
			1. userId
			2. chatId
	*/
	templateGetMateChatWithIdForUser = templateAllGetMateChatsForUser +
		` AND "MateChat"."Id" = $2`

	/*
		Order:
			1. chatId
			2. userId
	*/
	templateInsertDeletedMateChat = utl.RemoveAdjacentWs(`
		INSERT INTO "DeletedMateChat" ("ChatId", "UserId") 
			VALUES ($1, $2)`)

	/*
		Order:
			1. chatId
	*/
	templateRemoveDeletedMateChatByChatId = utl.RemoveAdjacentWs(`
		DELETE FROM "DeletedMateChat"
			WHERE "ChatId" = $1`)

	templateDeleteMateChat = utl.RemoveAdjacentWs(`
		DELETE FROM "MateChat" WHERE "Id" = $1`)
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

func (s *MateChatStorage) GetTableMateChatWithId(ctx context.Context, id uint64) (*table.MateChat, error) {
	sourceFunc := s.GetTableMateChatWithId
	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx,
				templateGetMateChatWithIdFromTable+`;`, id)
		},
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChat, err := tableMateChatFromRow(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateChat, nil
}

func (s *MateChatStorage) GetTableMateChatWithIdForUser(ctx context.Context, chatId, userId uint64) (*table.MateChat, error) {
	sourceFunc := s.GetTableMateChatWithIdForUser
	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx,
				templateGetTableMateChatWithIdForUser+`;`,
				chatId, userId)
		},
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChat, err := tableMateChatFromRow(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateChat, nil
}

func (s *MateChatStorage) GetMateChatWithIdForUser(ctx context.Context,
	userId, chatId uint64) (*domain.MateChat, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatWithIdForUser, err)
	}
	defer conn.Release()

	// TODO:! templateGetMateChatWithIdForUser

	// ***

	return nil, ErrNotImplemented
}

// -----------------------------------------------------------------------

func (s *MateChatStorage) DeleteMateChatForUser(ctx context.Context,
	userId, chatId uint64) error {
	/*
		Action List:
			If the chat is deleted from another user:
				1. Delete records from `DeletedMateChat`.
				2. Remove all messages.
				2. Remove mate chat.
			Else:
				1. Add record to `DeletedMateChat`.
				2. Remove entry from `Mate`.
	*/
	sourceFunc := s.DeleteMateChatForUser
	mateChat, err := s.GetTableMateChatWithIdForUser(ctx, chatId, userId)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	var secondUserId uint64 = 0
	if mateChat.FirstUserId == userId {
		secondUserId = mateChat.SecondUserId
	} else {
		secondUserId = mateChat.FirstUserId
	}

	// ***

	available, err := s.AvailableMateChatWithIdForUser(ctx, chatId, secondUserId)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	if !available {
		err = errors.Join(
			removeDeletedMateChatByChatIdInsideTx(ctx, tx, chatId),
			removeAllMessagesFromMateChatInsideTx(ctx, tx, chatId),
			deleteMateChatInsideTx(ctx, tx, chatId),
		)
	} else {
		err = errors.Join(
			insertDeletedMateChatInsideTx(ctx, tx, chatId, userId),
			deleteMateInsideTx(ctx, tx, userId, secondUserId),
		)
	}

	if err != nil {
		err = errors.Join(tx.Rollback(ctx)) // ?
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func insertMateChatWithoutReturningIdInsideTx(ctx context.Context, tx pgx.Tx,
	firstUserId uint64, secondUserId uint64) error {
	sourceFunc := insertMateChatWithoutReturningIdInsideTx

	err := insertForUserPairWithoutReturningIdInsideTx(ctx, tx,
		templateInsertMateChatWithoutReturningId,
		firstUserId, secondUserId,
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}

func insertDeletedMateChatInsideTx(ctx context.Context, tx pgx.Tx,
	chatId, userId uint64) error {
	sourceFunc := insertDeletedMateChatInsideTx

	cmdTag, err := tx.Exec(ctx, templateInsertDeletedMateChat+`;`,
		chatId, userId,
	)

	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func removeDeletedMateChatByChatIdInsideTx(ctx context.Context, tx pgx.Tx,
	chatId uint64) error {
	sourceFunc := removeDeletedMateChatByChatIdInsideTx

	cmdTag, err := tx.Exec(ctx,
		templateRemoveDeletedMateChatByChatId+`;`,
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

func tableMateChatFromRow(row pgx.Row) (*table.MateChat, error) {
	mateChat := table.MateChat{}
	err := row.Scan(
		&mateChat.Id,
		&mateChat.FirstUserId,
		&mateChat.SecondUserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(tableMateChatFromRow, err)
	}

	return &mateChat, nil
}

func deleteMateChatInsideTx(ctx context.Context, tx pgx.Tx,
	chatId uint64) error {
	sourceFunc := deleteMateChatInsideTx

	cmdTag, err := tx.Exec(ctx,
		templateDeleteMateChat+`;`,
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
