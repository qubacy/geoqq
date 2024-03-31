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

	passwordDoubleHash, err := a.passwordHashInHexToPasswordDoubleHash(
		input.PasswordHash)
	if err != nil {
		return utl.NewFuncError(a.assertUserByCredentialsExists, err)
	}

	// ***

	exists, err := a.domainStorage.HasUserByCredentials(ctx, input.Login, passwordDoubleHash)
	if err != nil {
		return utl.NewFuncError(a.assertUserByCredentialsExists,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	if !exists {
		return utl.NewFuncError(a.assertUserByCredentialsExists,
			ec.New(ErrIncorrectLoginOrPassword, ec.Client, ec.UserNotFound))
	}

	return nil
}

func (a *AuthService) assertUserWithNameNotExists(
	ctx context.Context, input dto.SignUpInp) error {

	exists, err := a.domainStorage.HasUserWithName(ctx, input.Login)
	if err != nil {
		return utl.NewFuncError(a.assertUserWithNameNotExists,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	if exists {
		return utl.NewFuncError(a.assertUserWithNameNotExists,
			ec.New(ErrUserWithThisLoginAlreadyExists, ec.Client, ec.UserAlreadyExist))
	}

	return nil
}
