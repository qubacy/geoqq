package impl

import (
	"context"
	"errors"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	"geoqq_http/internal/service"
	"geoqq_http/internal/service/dto"
	"geoqq_http/pkg/avatar"
	"geoqq_http/pkg/file"
	"geoqq_http/pkg/logger"
	"geoqq_http/pkg/token"
	utl "geoqq_http/pkg/utility"
	"time"
)

type AuthService struct {
	HasherAndStorages
	avatarGenerator avatar.AvatarGenerator

	accessTokenTTL  time.Duration
	refreshTokenTTL time.Duration
	tokenManager    token.TokenManager

	authParams AuthParams

	validators Validators
}

// without check?
func newAuthService(deps Dependencies) (*AuthService, error) {
	instance := &AuthService{
		HasherAndStorages: HasherAndStorages{
			enableCache: deps.EnableCache,
			cache:       deps.Cache,

			domainStorage: deps.DomainStorage,
			fileStorage:   deps.FileStorage,
			hashManager:   deps.HashManager,
		},
		avatarGenerator: deps.AvatarGenerator,

		accessTokenTTL:  deps.AccessTokenTTL,
		refreshTokenTTL: deps.RefreshTokenTTL,
		tokenManager:    deps.TokenManager,

		authParams: deps.AuthParams,
	}

	logger.Trace("access token ttl: %v", deps.AccessTokenTTL)
	logger.Trace("refresh token ttl: %v", deps.RefreshTokenTTL)

	// ***

	if err := instance.initializeValidators(); err != nil {
		return nil, utl.NewFuncError(newAuthService, err)
	}
	return instance, nil
}

// public
// -----------------------------------------------------------------------

func (a *AuthService) SignIn(ctx context.Context, input dto.SignInInp) (
	dto.SignInOut, error,
) {
	sourceFunc := a.SignIn
	nilResult := dto.MakeSignInOutEmpty()

	err := a.validateLoginAndPassword(input.Login, input.PasswordHash)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.ValidateAuthParamsFailed)
	}

	// some asserts

	err = a.assertSignInByLoginNotBlocked(ctx, input.Login) // using cache.
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}
	err = assertUserWithLoginNotDeleted(ctx, a.domainStorage, input.Login)
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}

	err = a.assertUserByCredentialsExists(ctx, input) // more strict check!
	if err != nil {
		if a.enableCache {
			side, _ := ec.UnwrapErrorsToLastSideAndCode(err)
			if side == ec.Client { // any error for which the client is to blame!
				if err := a.updateSignInCache(ctx, input.Login); err != nil {
					return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
						ec.Server, ec.CacheError)
				}
			}
		} else {
			logger.Warning("cache disabled")
		}

		return nilResult, utl.NewFuncError(sourceFunc, err)
	}

	// generate tokens

	userId, err := a.domainStorage.GetUserIdByByLogin(ctx, input.Login)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, clientCode)
	}

	return dto.MakeSignInOut(accessToken, refreshToken), nil
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (
	dto.SignUpOut, error,
) {
	sourceFunc := a.SignUp
	nilResult := dto.MakeSignUpOutEmpty()

	// fast assert

	clientIp := ctx.Value(service.AuthServiceContextClientIp).(string)
	err := a.assertSignUpByIpAddrNotBlocked(ctx, clientIp)
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}

	err = a.validateLoginAndPassword(input.Login, input.PasswordHash)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.ValidateAuthParamsFailed)
	}
	err = a.assertUserWithLoginNotExists(ctx, input.Login)
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}

	// create user

	avatarId, err := a.generateAndSaveAvatarForUser(ctx, input.Login) // with error for client!
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}
	passwordDoubleHash, err := passwordHashInHexToPasswordDoubleHash(
		a.hashManager, input.PasswordHash)
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}
	userId, err := a.domainStorage.InsertUser(ctx, input.Login, passwordDoubleHash, avatarId)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// limit frequent registration

	if a.enableCache {
		if err := a.updateSignUpCache(ctx, clientIp); err != nil {
			return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
				ec.Server, ec.CacheError)
		}

	} else {
		logger.Warning("cache disabled")
	}

	// generate tokens

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, clientCode)
	}

	return dto.MakeSignUpOut(accessToken, refreshToken), nil // OK
}

func (a *AuthService) RefreshTokens(ctx context.Context, refreshToken string) (
	dto.RefreshTokensOut, error,
) {
	sourceFunc := a.RefreshTokens
	nilResult := dto.MakeRefreshTokensOutEmpty()

	payload, err := a.tokenManager.ParseRefresh(refreshToken) // with validation!
	if err != nil {

		// believe that module is working correctly?!
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.ValidateRefreshTokenFailed)
	}

	err = a.identicalHashesForRefreshTokens(ctx, payload.UserId, refreshToken)
	if err != nil {
		return nilResult, utl.NewFuncError(sourceFunc, err)
	}

	// generate tokens

	accessToken, refreshToken, err := a.generateTokens(payload.UserId)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, payload.UserId, refreshToken)
	if err != nil {
		return nilResult, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, clientCode)
	}

	return dto.MakeRefreshTokensOut(accessToken, refreshToken), nil
}

// extra for middleware
// -----------------------------------------------------------------------

func (a *AuthService) WasUserWithIdDeleted(ctx context.Context, id uint64) (bool, error) {
	var err error = nil
	var wasDeleted bool = false

	if a.enableCache {
		wasDeleted, err = a.cache.Exists(ctx, authCacheKey.DeletedUser(id))
		if err != nil {
			logger.Warning("%v", err)
		} else {
			return wasDeleted, nil
		}
	}

	if !a.enableCache || err != nil {
		wasDeleted, err = a.domainStorage.WasUserDeleted(ctx, id)
		if err != nil {
			return false, ec.New(utl.NewFuncError(a.WasUserWithIdDeleted, err),
				ec.Server, ec.DomainStorageError)
		}
	}
	return wasDeleted, nil
}

// private
// -----------------------------------------------------------------------

func (a *AuthService) generateAndSaveAvatarForUser(ctx context.Context, login string) (uint64, error) {
	imageBytes, imageHash, err := a.generateAvatarForUser(login)
	if err != nil {
		return 0, utl.NewFuncError(a.generateAndSaveAvatarForUser, err)
	}

	// ***

	// TODO: combine into a transaction? How?

	avatarId, err := a.domainStorage.InsertServerGeneratedAvatar(ctx, imageHash)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(a.generateAndSaveAvatarForUser, err),
			ec.Server, ec.DomainStorageError)
	}

	err = a.fileStorage.SaveImage(ctx, file.NewPngImageFromBytes(avatarId, imageBytes))
	if err != nil {
		err = errors.Join(err, a.domainStorage.DeleteAvatarWithId(ctx, avatarId))
		return 0, ec.New(utl.NewFuncError(a.generateAndSaveAvatarForUser, err),
			ec.Server, ec.FileStorageError)
	}

	return avatarId, nil
}

func (a *AuthService) generateAvatarForUser(login string) ([]byte, string, error) {
	sourceFunc := a.generateAvatarForUser
	image, err := a.avatarGenerator.NewForUser(login)
	if err != nil {
		return nil, "", ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.AvatarGeneratorError)
	}

	// ***

	imageBytes, err := utl.ImageToPngBytes(image)
	if err != nil {
		return nil, "", ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.AvatarGeneratorError)
	}

	imageHash, err := a.hashManager.NewFromBytes(imageBytes) // in a separate function?
	if err != nil {
		return nil, "", ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.HashManagerError)
	}

	return imageBytes, imageHash, nil
}

// -----------------------------------------------------------------------

func (a *AuthService) generateTokens(userId uint64) (string, string, error) {
	payloadForAccess := token.MakePayload(userId, token.ForAccess)
	payloadForRefresh := token.MakePayload(userId, token.ForRefresh)

	access, errForAccess := a.tokenManager.New(payloadForAccess, a.accessTokenTTL)
	refresh, errForRefresh := a.tokenManager.New(payloadForRefresh, a.refreshTokenTTL)
	err := errors.Join(errForAccess, errForRefresh)

	if err != nil {
		return "", "", utl.NewFuncError(a.generateTokens, err)
	}
	return access, refresh, nil
}

func (a *AuthService) updateHashRefreshToken(ctx context.Context,
	userId uint64, refreshToken string) (int, error) { // <--- with client code!
	sourceFunc := a.updateHashRefreshToken

	hashRefreshToken, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return ec.HashManagerError, utl.NewFuncError(sourceFunc, err)
	}

	err = a.domainStorage.UpdateHashRefreshTokenAndSomeTimes(ctx, userId, hashRefreshToken)
	if err != nil {
		return ec.DomainStorageError, utl.NewFuncError(sourceFunc, err)
	}

	return ec.NoError, nil
}

// -----------------------------------------------------------------------

func (a *AuthService) identicalHashesForRefreshTokens(ctx context.Context,
	userId uint64, refreshToken string) error {
	sourceFunc := a.identicalHashesForRefreshTokens

	currentHash, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.HashManagerError)
	}

	// there is no token for the deleted user!
	storageHash, err := a.domainStorage.GetHashRefreshToken(ctx, userId)
	if err != nil {
		return ec.NewErrorForClient(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	if currentHash != storageHash { // client side...
		return ec.NewErrorForClient(ErrNotSameHashesForRefreshTokens,
			ec.Client, ec.ValidateRefreshTokenFailed)
	}
	return nil
}
