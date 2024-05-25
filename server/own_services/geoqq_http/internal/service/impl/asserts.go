package impl

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	domainStorage "geoqq_http/internal/storage/domain"
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
