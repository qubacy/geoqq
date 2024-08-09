package postgre

import (
	domain "common/pkg/domain/geoqq"
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
)

func MateChatFromQueryResult(queryResult wrappedPgxpool.QueryResultScanner) (
	*domain.MateChat, error) {

	sourceFunc := MateChatFromQueryResult

	mateChat := domain.NewEmptyMateChat()
	lastMessageExists := false

	_ = template.GetAllMateChatsForUser // main template!

	err := queryResult.Scan(
		&mateChat.Id, &mateChat.UserId, &mateChat.LastActionTime,
		&mateChat.NewMessageCount, &lastMessageExists,
		nil, nil, nil, nil, nil, // <--- skip fields!
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	// ***

	var lastMessage *domain.MateMessage = nil
	if lastMessageExists {
		lastMessage, err = LastMateChatMessageFromQueryResult(queryResult)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		mateChat.LastActionTime = lastMessage.Time // upd!
	}

	mateChat.LastMessage = lastMessage
	return mateChat, nil
}

func LastMateChatMessageFromQueryResult(queryResult wrappedPgxpool.QueryResultScanner) (
	*domain.MateMessage, error) {

	sourceFunc := LastMateChatMessageFromQueryResult

	mateMessage := new(domain.MateMessage)
	err := queryResult.Scan(
		nil, nil, nil, nil, nil,
		&mateMessage.Id, &mateMessage.Text,
		&mateMessage.Time, &mateMessage.UserId,
		&mateMessage.Read,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return mateMessage, nil
}

// -----------------------------------------------------------------------
