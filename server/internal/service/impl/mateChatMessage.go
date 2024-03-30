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

// constraints
// -----------------------------------------------------------------------

func assertMateChatExists(ctx context.Context,
	domainStorage domainStorage.Storage, chatId uint64) error {

	exists, err := domainStorage.HasMateChatWithId(ctx, chatId)
	if err != nil {
		return utl.NewFuncError(assertMateChatExists,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return utl.NewFuncError(assertMateChatExists,
			ec.New(ErrMateChatNotFound, ec.Client, ec.MateChatNotFound))
	}

	return nil
}

func assertMateChatAvailableForUser(ctx context.Context,
	domainStorage domainStorage.Storage, userId, chatId uint64) error {

	available, err := domainStorage.AvailableMateChatWithIdForUser(ctx, chatId, userId)
	if err != nil {
		return utl.NewFuncError(assertMateChatAvailableForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !available {
		return utl.NewFuncError(assertMateChatAvailableForUser,
			ec.New(ErrMateChatNotAvailable, ec.Client, ec.MateChatNotAvailable))
	}

	return nil
}
