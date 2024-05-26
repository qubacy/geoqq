package impl

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/infra/msgs"
	domainStorage "geoqq_http/internal/storage/domain"
	"time"
)

type MateChatMessageService struct {
	domainStorage domainStorage.Storage
	chatParams    ChatParams
	msgs          msgs.Msgs
}

func newMateChatMessageService(deps Dependencies) *MateChatMessageService {
	instance := &MateChatMessageService{
		domainStorage: deps.DomainStorage,
		chatParams:    deps.ChatParams,
		msgs:          deps.Msgs,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *MateChatMessageService) ReadMateChatMessagesByChatId(ctx context.Context,
	userId, chatId, offset, count uint64) (domain.MateMessageList, error) {

	err := assertMateChatWithIdExists(ctx, s.domainStorage, chatId)
	if err != nil {
		return nil, utl.NewFuncError(s.ReadMateChatMessagesByChatId, err)
	}
	err = assertMateChatAvailableForUser(ctx, s.domainStorage, userId, chatId)
	if err != nil {
		return nil, utl.NewFuncError(s.ReadMateChatMessagesByChatId, err)
	}

	// ***

	mateMessages, err := s.domainStorage.ReadMateChatMessagesByChatId(
		ctx, userId, chatId,
		count, offset,
	)

	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.ReadMateChatMessagesByChatId, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return mateMessages, nil
}

func (s *MateChatMessageService) AddMessageToMateChat(ctx context.Context,
	userId, chatId uint64, text string) error {

	if len(text) > int(s.chatParams.MaxMessageLength) {
		return ec.New(ErrMessageTooLong(s.chatParams.MaxMessageLength),
			ec.Client, ec.MateMessageTooLong)
	}

	// some asserts

	err := assertMateChatWithIdExists(ctx, s.domainStorage, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat, err)
	}
	err = assertMateChatAvailableForUser(ctx, s.domainStorage, userId, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat, err)
	}

	mateChat, err := s.domainStorage.GetTableMateChatWithId(ctx, chatId)
	if err != nil {
		return ec.New(utl.NewFuncError(s.AddMessageToMateChat, err),
			ec.Server, ec.DomainStorageError)
	}
	err = assertUsersAreMates(ctx, s.domainStorage,
		mateChat.FirstUserId, mateChat.SecondUserId) // one of these `userId`
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat, err)
	}

	// write to database

	mateMsgId, err := s.domainStorage.InsertMateChatMessage(ctx, chatId, userId, text)
	if err != nil {
		return ec.New(utl.NewFuncError(s.AddMessageToMateChat, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	if s.msgs != nil {
		interlocutorId := mateChat.FirstUserId
		if userId == interlocutorId {
			interlocutorId = mateChat.SecondUserId
		}

		s.msgs.SendMateMessage(ctx, msgs.EventAddedMateMessage,
			interlocutorId, chatId, &domain.MateMessage{
				Id:     mateMsgId,
				Text:   text,
				Time:   time.Now().UTC(), // small inaccuracy!
				UserId: userId,
				Read:   false,
			})
	} else {
		logger.Warning("msgs disabled")
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return nil
}
