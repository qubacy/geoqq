package impl

import (
	"context"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	domainStorage "geoqq_http/internal/storage/domain"
	utl "geoqq_http/pkg/utility"
)

func assertUserWithLoginNotDeleted(ctx context.Context,
	storage domainStorage.Storage, login string) error {

	wasDeleted, err := storage.WasUserWithLoginDeleted(ctx, login)
	if err != nil {
		return ec.New(utl.NewFuncError(assertUserWithLoginNotDeleted, err),
			ec.Server, ec.DomainStorageError)
	}

	if wasDeleted {
		return ec.New(ErrUserWithLoginHasBeenDeleted,
			ec.Client, ec.UserWasPreviouslyDeleted)
	}

	return nil
}
