package impl

import (
	"context"
	"geoqq/internal/domain"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type MateChatMessageService struct {
	domainStorage domainStorage.Storage
}

func newMateChatMessageService(deps Dependencies) *MateChatMessageService {
	instance := &MateChatMessageService{
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *MateChatMessageService) ReadMateChatMessagesByChatId(ctx context.Context,
	userId, chatId, offset, count uint64) (domain.MateMessageList, error) {

	err := assertMateChatExists(ctx, s.domainStorage, chatId)
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
		return nil, utl.NewFuncError(s.ReadMateChatMessagesByChatId,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return mateMessages, nil
}

// TODO: check length text message!!!

func (s *MateChatMessageService) AddMessageToMateChat(ctx context.Context,
	userId, chatId uint64, text string) error {

	err := assertMateChatExists(ctx, s.domainStorage, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat, err)
	}

	err = assertMateChatAvailableForUser(ctx, s.domainStorage, userId, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat, err)
	}

	// write to database!

	_, err = s.domainStorage.InsertMateChatMessage(ctx, chatId, userId, text)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return nil
}
