package postgre

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"

	"github.com/jackc/pgx/v4/pgxpool"
)

type MateDatabase struct {
	pool *pgxpool.Pool
}

func newMateDatabase(pool *pgxpool.Pool) *MateDatabase {
	return &MateDatabase{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	/*
		Orders:
			1. mateChatId
			2. userId
	*/
	templateSelectMateIdByChatId = utl.RemoveAdjacentWs(`
		SELECT 
			case 
				when "FirstUserId" = $2 
					then "SecondUserId"
					else "FirstUserId"
			end as "InterlocutorId"
		FROM "MateChat" WHERE "Id" = $1`)

	templateSelectMateMessageById = utl.RemoveAdjacentWs(`
		SELECT * FROM "MateMessage"
			WHERE "Id" = $1`)

	/*
		Orders:
			1. mateChatId
	*/
	templateHasMateChatWithId = utl.RemoveAdjacentWs(`
		SELECT 
			case 
				WHEN COUNT(*) >= 1
					then true
					else false
			end as "Has"
		FROM "MateChat" WHERE "Id" = $1`)
)

// public
// -----------------------------------------------------------------------

func (m *MateDatabase) GetMateMessageById(ctx context.Context,
	mateMessageId uint64) (*domain.MateMessage, error) {
	sourceFunc := m.GetMateMessageById

	row, err := queryRow(ctx, m.pool,
		templateSelectMateMessageById,
		mateMessageId)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	mm, err := scanMateMessage(row)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return mm, nil
}

func (m *MateDatabase) HasMateChatWithId(ctx context.Context, chatId uint64) (bool, error) {
	sourceFunc := m.HasMateChatWithId

	row, err := queryRow(ctx, m.pool,
		templateHasMateChatWithId, chatId)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}

	has, err := wrappedPgxpool.ScanBool(row, sourceFunc)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	return has, nil
}

func (m *MateDatabase) InsertMateMessage(ctx context.Context, chatId uint64,
	fromUserId uint64, text string) (uint64, error) {
	sourceFunc := m.InsertMateMessage

	row, err := queryRow(ctx, m.pool,
		template.InsertMateChatMessage, // template
		chatId, fromUserId, text)       // args

	var gmId uint64
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error {
			gmId, err = wrappedPgxpool.ScanUint64(row, sourceFunc)
			return err // nil eq ok!
		})
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return gmId, nil
}

func (m *MateDatabase) GetMateIdByChatId(ctx context.Context,
	userId, chatId uint64) (uint64, error) {
	sourceFunc := m.GetMateIdByChatId

	row, err := queryRow(ctx, m.pool,
		templateSelectMateIdByChatId,
		chatId, userId)

	var interlocutorId uint64
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error {
			interlocutorId, err = wrappedPgxpool.ScanUint64(row, sourceFunc)
			return err
		})
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return interlocutorId, nil
}

func (m *MateDatabase) GetMateChatWithIdForUser(ctx context.Context,
	userId, chatId uint64) (*domain.MateChat, error) {
	sourceFunc := m.GetMateChatWithIdForUser

	row, err := queryRow(ctx, m.pool,
		template.GetMateChatWithIdForUser,
		userId, chatId)

	var mateChat domain.MateChat
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error {
			interlocutorId, err = wrappedPgxpool.ScanUint64(row, sourceFunc)
			return err
		})
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return &mateChat, nil
}

// scan
// -----------------------------------------------------------------------

func scanMateMessage(scanner wrappedPgxpool.QueryResultScanner) (
	*domain.MateMessage, error) {
	sourceFunc := scanMateMessage

	mm := domain.MateMessage{}
	err := scanner.Scan(
		&mm.Id, &mm.ChatId,
		&mm.UserId, &mm.Text,
		&mm.Time, &mm.Read)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return &mm, nil
}
