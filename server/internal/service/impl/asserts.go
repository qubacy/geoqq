package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

func assertUserWithNameNotDeleted(ctx context.Context,
	storage domainStorage.Storage, name string) error {

	wasDeleted, err := storage.WasUserWithNameDeleted(ctx, name)
	if err != nil {
		return utl.NewFuncError(assertUserWithNameNotDeleted,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	if wasDeleted {
		return utl.NewFuncError(assertUserWithNameNotDeleted,

			// hide this information for user?
			// ec.New(ErrIncorrectLoginOrPassword, ec.Client, ec.UserNotFound))

			ec.New(ErrUserHasBeenDeleted, ec.Client, ec.UserNotFound))
	}

	return nil
}

func assertUserWithIdNotDeleted(ctx context.Context,
	storage domainStorage.Storage, id uint64) error {

	wasDeleted, err := storage.WasUserDeleted(ctx, id)
	if err != nil {
		return utl.NewFuncError(assertUserWithIdNotDeleted,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	if wasDeleted {
		return utl.NewFuncError(assertUserWithIdNotDeleted,
			ec.New(ErrUserHasBeenDeleted, ec.Client, ec.UserNotFound))
	}

	return nil
}
