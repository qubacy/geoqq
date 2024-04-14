package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

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
		return ec.New(utl.NewFuncError(assertMateChatAvailableForUser, err),
			ec.Server, ec.DomainStorageError)
	}
	if !available {
		return ec.New(ErrMateChatNotAvailable,
			ec.Client, ec.MateChatNotAvailable)
	}

	return nil
}
