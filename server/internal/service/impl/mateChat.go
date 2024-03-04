package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type MateChatService struct {
	domainStorage domainStorage.Storage
}

func newMateChatService(deps Dependencies) *MateChatService {
	instance := &MateChatService{
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *MateChatService) AddMessageToMateChat(ctx context.Context,
	userId, chatId uint64, text string) error {

	// TODO: Удален чат или нет?
	// Пользователь принадлежит чату?

	exists, err := s.domainStorage.HasMateChatWithId(ctx, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(ErrMateChatNotFound, ec.Client, ec.MateChatNotFound))
	}

	// ***

	_, err = s.domainStorage.InsertMateChatMessage(ctx, chatId, userId, text)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return nil
}
