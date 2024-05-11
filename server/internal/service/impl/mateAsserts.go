package impl

import (
	"context"
	ec "geoqq/internal/pkg/errorForClient/impl"
	domainStorage "geoqq/internal/storage/domain"
	utl "geoqq/pkg/utility"
)

func assertMateChatWithIdExists(ctx context.Context,
	domainStorage domainStorage.Storage, chatId uint64) error {

	exists, err := domainStorage.HasMateChatWithId(ctx, chatId)
	if err != nil {
		return ec.New(utl.NewFuncError(assertMateChatWithIdExists, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return ec.New(ErrMateChatNotFound,
			ec.Client, ec.MateChatNotFound)
	}

	return nil
}

func assertMateChatAvailableForUser(ctx context.Context,
	domainStorage domainStorage.Storage, userId, chatId uint64) error {

	available, err := domainStorage.AvailableMateChatWithIdForUser(ctx, chatId, userId)
	if err != nil {
		return ec.New(utl.NewFuncError(assertMateChatAvailableForUser, err),
			ec.Server, ec.DomainStorageError)
	}
	if !available {
		return ec.New(ErrMateChatNotAvailable,
			ec.Client, ec.MateChatNotAvailable)
	}

	return nil
}

func assertUsersAreMates(ctx context.Context,
	domainStorage domainStorage.Storage, firstUserId, secondUserId uint64) error {
	areMates, err := domainStorage.AreMates(ctx, firstUserId, secondUserId)
	if err != nil {
		return ec.New(utl.NewFuncError(assertUsersAreMates, err),
			ec.Server, ec.DomainStorageError)
	}
	if !areMates {
		return ec.New(ErrUsersAreNotMates,
			ec.Client, ec.UsersAreNotMates)
	}

	return nil
}
