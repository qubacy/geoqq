package impl

import (
	"context"
	"geoqq/internal/domain"
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

	available, err := s.domainStorage.AvailableMateChatWithIdForUser(ctx, userId, chatId)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !available {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(ErrMateChatNotAvailable, ec.Client, ec.MateChatNotAvailable))
	}

	// write to database!

	_, err = s.domainStorage.InsertMateChatMessage(ctx, chatId, userId, text)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToMateChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return nil
}

// -----------------------------------------------------------------------

func (s *MateChatService) GetMateChatsForUser(ctx context.Context,
	userId, offset, count uint64) (domain.MateChatList, error) {
	mateChats, err := s.domainStorage.GetMateChatsForUser(ctx, userId, offset, count)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatsForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return mateChats, nil // same types!
}
