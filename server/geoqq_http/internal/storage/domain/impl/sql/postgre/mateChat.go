package postgre

import (
	"context"
	"errors"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/domain/table"
	utl "geoqq_http/pkg/utility"

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
		VALUES($1, $2) 
			ON CONFLICT DO NOTHING`) // see index `unique_mate_chat_ids_comb`

	templateInsertMateChat = templateInsertMateChatWithoutReturningId +
		` RETURNING "Id"`

	templateHasMateChatWithId = utl.RemoveAdjacentWs(`
		SELECT case
					when COUNT(*) = 1 then true
					else false
				end as "Has"
		FROM "MateChat"
		WHERE "Id" = $1`)

	templateGetTableMateChatWithId = utl.RemoveAdjacentWs(`
		SELECT "Id", "FirstUserId", "SecondUserId" 
			FROM "MateChat" WHERE "Id" = $1`)

	/*
		Order:
			1. userId
	*/
	templateGetAllTableMateChatsForUser = utl.RemoveAdjacentWs(`
		SELECT "Id", "FirstUserId", "SecondUserId" 
    		FROM "MateChat" 
				WHERE ("FirstUserId" = $1 OR "SecondUserId" = $1)`)
	/*
		Order:
			1. userId
			2. chatId
	*/
	templateGetTableMateChatWithIdForUser = `` +
		templateGetAllTableMateChatsForUser + ` AND "Id" = $2`

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
	templateGetAllMateChatsForUser = utl.RemoveAdjacentWs(`
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
	templateGetMateChatsForUser = templateGetAllMateChatsForUser +
		` ORDER BY "LastMessageTime" DESC NULLS LAST,
				   "LastMessageId" DESC NULLS LAST /* ? */
			LIMIT $2 OFFSET $3`

	/*
		Order:
			1. userId
			2. chatId
	*/
	templateGetMateChatWithIdForUser string = templateGetAllMateChatsForUser +
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

	/*
		Order:
			1. userId
	*/
	templateGetAvailableMateChatsForUser = utl.RemoveAdjacentWs(`
		WITH "MateChatNotDeletedFor1stUser" AS (
			SELECT 
				"Id" AS "MateChatId",
				case 
					when "SecondUserId" = $1
					then "FirstUserId" else "SecondUserId"
				end as "2ndUserId"

			FROM "MateChat"
			LEFT JOIN "DeletedMateChat" ON ("ChatId" = "Id" AND
											"UserId" = $1)
			WHERE ("FirstUserId" = $1 OR
				   "SecondUserId" = $1) AND 
						"DeletedMateChat"."ChatId" IS NULL /* filter! */
		)
		SELECT 
			"MateChatNotDeletedFor1stUser".*,
			case
				when "DeletedMateChat"."ChatId" IS NULL 
				then FALSE else TRUE
			end as "DeletedFor2nd"
		FROM "MateChatNotDeletedFor1stUser"
		LEFT JOIN "DeletedMateChat" ON 
			"MateChatId" = "ChatId"`)

	/*
		Order:
			1. firstUserId
			2. secondUserId
	*/
	templateRemoveDeletedMateChatsForUsers = utl.RemoveAdjacentWs(`
		DELETE FROM "DeletedMateChat" WHERE "ChatId" IN (
			SELECT "Id" FROM "MateChat" WHERE (
				("FirstUserId" = $1 OR "SecondUserId" = $1) AND
				("FirstUserId" = $2 OR "SecondUserId" = $2)
				))`)
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
	sourceFunc := s.AvailableMateChatWithIdForUser
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx,
		templateAvailableMateChatWithIdForUser+`;`,
		chatId, userId,
	)

	var available bool = false
	err = row.Scan(&available)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
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
		mateChat, err := mateChatFromQueryResult(rows)
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
				templateGetTableMateChatWithId+`;`, id)
		},
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChat, err := tableMateChatFromQueryResult(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateChat, nil
}

func (s *MateChatStorage) GetTableMateChatWithIdForUser(ctx context.Context, chatId, userId uint64) (
	*table.MateChat, error,
) {
	sourceFunc := s.GetTableMateChatWithIdForUser
	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx,
				templateGetTableMateChatWithIdForUser+`;`,
				userId, chatId,
			)
		},
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChat, err := tableMateChatFromQueryResult(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateChat, nil
}

func (s *MateChatStorage) GetMateChatWithIdForUser(ctx context.Context,
	userId, chatId uint64) (*domain.MateChat, error) {
	sourceFunc := s.GetMateChatWithIdForUser
	row, err := queryRowWithConnectionAcquire(s.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx,
				templateGetMateChatWithIdForUser+`;`,
				userId, chatId,
			)
		},
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChat, err := mateChatFromQueryResult(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mateChat, nil
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

	// available to user and not previously deleted!
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

	available, err := s.AvailableMateChatWithIdForUser(ctx, chatId, secondUserId) // !
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	err = stepsToDeleteMateChatForUserInsideTx(ctx, tx, available,
		chatId, userId, secondUserId)

	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx)) // ?
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

// transaction!
// -----------------------------------------------------------------------

func stepsToDeleteMateChatForUserInsideTx(ctx context.Context, tx pgx.Tx,
	availableForSecond bool, chatId, firstUserId, secondUserId uint64) error {
	if !availableForSecond {
		return errors.Join(
			removeDeletedMateChatByChatIdInsideTx(ctx, tx, chatId),
			removeAllMessagesFromMateChatInsideTx(ctx, tx, chatId),
			deleteMateChatInsideTx(ctx, tx, chatId),
		)
	}

	return errors.Join(
		insertDeletedMateChatInsideTx(ctx, tx, chatId, firstUserId),
		deleteMateInsideTx(ctx, tx, firstUserId, secondUserId),
	)
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
	return insertInsideTx(ctx, insertDeletedMateChatInsideTx,
		tx, templateInsertDeletedMateChat, chatId, userId,
	)
}

func removeDeletedMateChatByChatIdInsideTx(ctx context.Context, tx pgx.Tx,
	chatId uint64) error {
	return deleteInsideTx(ctx, removeDeletedMateChatByChatIdInsideTx,
		tx, templateRemoveDeletedMateChatByChatId, chatId,
	)
}

func removeDeletedMateChatsForUsersInsideTx(ctx context.Context, tx pgx.Tx,
	firstUserId, secondUserId uint64) error {
	return deleteInsideTx(ctx, removeDeletedMateChatsForUsersInsideTx,
		tx, templateRemoveDeletedMateChatsForUsers,
		firstUserId, secondUserId,
	)
}

// queries by conn
// -----------------------------------------------------------------------

func getAvailableMateChatsForFirstUser(conn *pgxpool.Conn, ctx context.Context, userId uint64) (
	[]*domain.AvailableMateChatForFirst, error,
) {
	sourceFunc := getAvailableMateChatsForFirstUser
	rows, err := conn.Query(ctx,
		templateGetAvailableMateChatsForUser+`;`, userId,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mateChats := []*domain.AvailableMateChatForFirst{}
	for rows.Next() {
		mateChat, err := availableMateChatFromQueryResult(rows)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		mateChats = append(mateChats, mateChat)
	}

	return mateChats, nil
}

// convert
// -----------------------------------------------------------------------

func availableMateChatFromQueryResult(queryResult QueryResultScanner) (
	*domain.AvailableMateChatForFirst, error) {
	mateChat := &domain.AvailableMateChatForFirst{}
	err := queryResult.Scan(
		&mateChat.Id,
		&mateChat.SecondUserId,
		&mateChat.DeletedForSecond,
	)
	if err != nil {
		return nil, utl.NewFuncError(
			availableMateChatFromQueryResult, err)
	}

	return mateChat, nil
}

func mateChatFromQueryResult(queryResult QueryResultScanner) (*domain.MateChat, error) {
	mateChat := domain.NewEmptyMateChat()
	lastMessageExists := false

	err := queryResult.Scan(
		&mateChat.Id, &mateChat.UserId,
		&mateChat.NewMessageCount,
		&lastMessageExists,
		nil, nil, nil, nil, // <--- skip fields!
	)
	if err != nil {
		return nil, utl.NewFuncError(mateChatFromQueryResult, err)
	}

	// ***

	var lastMessage *domain.MateMessage = nil
	if lastMessageExists {
		lastMessage, err = lastMateChatMessageFromQueryResult(queryResult)
		if err != nil {
			return nil, utl.NewFuncError(mateChatFromQueryResult, err)
		}
	}

	mateChat.LastMessage = lastMessage
	return mateChat, nil
}

func lastMateChatMessageFromQueryResult(queryResult QueryResultScanner) (*domain.MateMessage, error) {
	mateMessage := new(domain.MateMessage)
	err := queryResult.Scan(
		nil, nil, nil, nil,
		&mateMessage.Id, &mateMessage.Text,
		&mateMessage.Time, &mateMessage.UserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(
			lastMateChatMessageFromQueryResult, err)
	}

	return mateMessage, nil
}

func tableMateChatFromQueryResult(queryResult QueryResultScanner) (*table.MateChat, error) {
	mateChat := table.MateChat{}
	err := queryResult.Scan(
		&mateChat.Id,
		&mateChat.FirstUserId,
		&mateChat.SecondUserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(tableMateChatFromQueryResult, err)
	}

	return &mateChat, nil
}
