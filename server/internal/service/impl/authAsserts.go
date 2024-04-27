package impl

import (
	"context"
	"geoqq/internal/service/dto"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

// asserts
// -----------------------------------------------------------------------

// Returning a compound error!
func (a *AuthService) assertUserByCredentialsExists(
	ctx context.Context, input dto.SignInInp) error {
	sourceFunc := a.assertUserByCredentialsExists
	passwordDoubleHash, err :=
		passwordHashInHexToPasswordDoubleHash(a.hashManager, input.PasswordHash)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	exists, err := a.domainStorage.HasUserByCredentials(ctx, input.Login, passwordDoubleHash)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return ec.New(ErrIncorrectLoginOrPassword,
			ec.Client, ec.UserByCredentialsNotFound)
	}
	return nil
}

func (a *AuthService) assertUserWithNameNotExists(
	ctx context.Context, login string) error {
	sourceFunc := a.assertUserWithNameNotExists

	exists, err := a.domainStorage.HasUserWithName(ctx, login)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if exists {
		return ec.New(ErrUserWithThisLoginAlreadyExists,
			ec.Client, ec.UserWithNameAlreadyExists)
	}
	return nil
}
