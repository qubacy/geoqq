package impl

import (
	"context"
	ec "geoqq/internal/pkg/errorForClient/impl"
	domainStorage "geoqq/internal/storage/domain"
	utl "geoqq/pkg/utility"
)

func assertUserWithNameNotDeleted(ctx context.Context,
	storage domainStorage.Storage, name string) error {

	wasDeleted, err := storage.WasUserWithNameDeleted(ctx, name)
	if err != nil {
		return ec.New(utl.NewFuncError(assertUserWithNameNotDeleted, err),
			ec.Server, ec.DomainStorageError)
	}

	if wasDeleted {
		return ec.New(ErrUserWithNameHasBeenDeleted,
			ec.Client, ec.UserWasPreviouslyDeleted)
	}

	return nil
}
