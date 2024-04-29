package impl

import (
	"context"
	"geoqq/internal/domain"
	ec "geoqq/internal/pkg/errorForClient/impl"
	domainStorage "geoqq/internal/storage/domain"
	utl "geoqq/pkg/utility"
)

type MateChatService struct {
	domainStorage domainStorage.Storage
	generalParams GeneralParams
}

func newMateChatService(deps Dependencies) *MateChatService {
	instance := &MateChatService{
		domainStorage: deps.DomainStorage,
		generalParams: deps.GeneralParams,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *MateChatService) GetMateChat(ctx context.Context, chatId, userId uint64) (
	*domain.MateChat, error,
) {
	sourceFunc := s.GetMateChat

	err := assertMateChatWithIdExists(ctx, s.domainStorage, chatId)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	err = assertMateChatAvailableForUser(ctx, s.domainStorage, userId, chatId)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	// ***

	mateChat, err := s.domainStorage.GetMateChatWithIdForUser(ctx, userId, chatId)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	return mateChat, nil
}

func (s *MateChatService) GetMateChatsForUser(ctx context.Context,
	userId, offset, count uint64) (domain.MateChatList, error) {

	if count > s.generalParams.MaxPageSize {
		return nil, ec.New(ErrCountMoreThanPermissible,
			ec.Client, ec.CountMoreThanPermissible)
	}

	// ***

	mateChats, err := s.domainStorage.GetMateChatsForUser(ctx, userId, offset, count)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetMateChatsForUser, err),
			ec.Server, ec.DomainStorageError)
	}

	return mateChats, nil // same types!
}

func (s *MateChatService) DeleteMateChatForUser(ctx context.Context,
	chatId, userId uint64) error {
	/*
		Action List:
			1. Is there a mate chat?
			2. Mate chat has not been previously deleted for the user.
			3. To domain storage...
	*/
	sourceFunc := s.DeleteMateChatForUser

	err := assertMateChatWithIdExists(ctx, s.domainStorage, chatId)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	// important!
	err = assertMateChatAvailableForUser(ctx, s.domainStorage, userId, chatId)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = s.domainStorage.DeleteMateChatForUser(ctx, userId, chatId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	return nil
}
