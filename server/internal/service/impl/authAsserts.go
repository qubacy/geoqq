package impl

import (
	"context"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/internal/service/dto"
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"
)

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

func (a *AuthService) assertUserWithLoginNotExists(
	ctx context.Context, login string) error {
	sourceFunc := a.assertUserWithLoginNotExists

	exists, err := a.domainStorage.HasUserWithLogin(ctx, login)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if exists {
		return ec.New(ErrUserWithThisLoginAlreadyExists,
			ec.Client, ec.UserWithLoginAlreadyExists)
	}
	return nil
}

// From Cache
// -----------------------------------------------------------------------

func (a *AuthService) assertSignInByLoginNotBlocked(
	ctx context.Context, login string) error {
	if !a.enableCache {
		logger.Warning("cache disabled")
		return nil
	}

	loginBlocked, err := a.cache.Exists(ctx, authCacheKey.SignInByLoginBlocked(login))
	if err != nil {
		return ec.New(utl.NewFuncError(a.assertSignInByLoginNotBlocked, err),
			ec.Server, ec.CacheError)
	}
	if loginBlocked {
		return ec.New(ErrSignInByLoginIsBlocked(login),
			ec.Client, ec.SignInByLoginBlocked)
	}
	return nil
}

func (a *AuthService) assertSignUpByIpAddrNotBlocked(
	ctx context.Context, ipAddr string) error {
	if !a.enableCache {
		logger.Warning("cache disabled")
		return nil
	}

	ipAddrBlocked, err := a.cache.Exists(ctx, authCacheKey.SignUpByIpAddrBlocked(ipAddr))
	if err != nil {
		return ec.New(utl.NewFuncError(a.assertSignUpByIpAddrNotBlocked, err),
			ec.Server, ec.CacheError)
	}
	if ipAddrBlocked {
		return ec.New(ErrSignUpByIpAddrBlocked(ipAddr),
			ec.Client, ec.SignUpByIpAddrBlocked)
	}
	return nil
}
