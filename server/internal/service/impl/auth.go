package impl

import (
	"context"
	"encoding/hex"
	"geoqq/internal/service/dto"
	"geoqq/pkg/avatar"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	"geoqq/pkg/token"
	utl "geoqq/pkg/utility"
	"regexp"
	"time"
)

type AuthService struct {
	accessTokenTTL  time.Duration
	refreshTokenTTL time.Duration
	tokenManager    token.TokenManager
	avatarGenerator avatar.AvatarGenerator
	HasherAndStorages

	validators map[string]*regexp.Regexp
}

// without check?
func newAuthService(deps Dependencies) *AuthService {
	instance := &AuthService{
		accessTokenTTL:  deps.AccessTokenTTL,
		refreshTokenTTL: deps.RefreshTokenTTL,
		tokenManager:    deps.TokenManager,
		avatarGenerator: deps.AvatarGenerator,

		HasherAndStorages: HasherAndStorages{
			fileStorage:   deps.FileStorage,
			domainStorage: deps.DomainStorage,
			hashManager:   deps.HashManager,
		},
	}

	// ***

	instance.initializeValidators()
	return instance
}

// public
// -----------------------------------------------------------------------

func (a *AuthService) SignIn(ctx context.Context, input dto.SignInInp) (
	dto.SignInOut, error,
) {
	err := a.validateLoginAndPassword(input.Login, input.PasswordHash)
	if err != nil {
		return a.signInWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}

	// some asserts

	err = assertUserWithNameNotDeleted(ctx, a.domainStorage, input.Login)
	if err != nil {
		return dto.SignInOut{}, utl.NewFuncError(a.SignIn, err)
	}
	err = a.assertUserByCredentialsExists(ctx, input)
	if err != nil {
		return dto.SignInOut{}, utl.NewFuncError(a.SignIn, err)
	}

	// generate tokens

	userId, err := a.domainStorage.GetUserIdByByName(ctx, input.Login)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.DomainStorageError)
	}

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return a.signInWithError(err, ec.Server, clientCode)
	}

	return dto.MakeSignInOut(accessToken, refreshToken), nil
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (
	dto.SignUpOut, error,
) {
	err := a.validateLoginAndPassword(input.Login, input.PasswordHash)
	if err != nil {
		return a.signUpWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}

	// some asserts

	err = a.assertUserWithNameNotExists(ctx, input)
	if err != nil {
		return dto.SignUpOut{}, utl.NewFuncError(a.SignUp, err)
	}

	// create user

	avatarId, err := a.generateAndSaveAvatarForUser(ctx, input.Login) // with error for client!
	if err != nil {
		return dto.MakeSignUpOutEmpty(), utl.NewFuncError(a.SignUp, err)
	}
	passwordDoubleHash, err := a.passwordHashInHexToPasswordDoubleHash(
		input.PasswordHash)
	if err != nil {
		return dto.SignUpOut{}, utl.NewFuncError(a.SignUp, err)
	}
	userId, err := a.domainStorage.InsertUser(ctx, input.Login, passwordDoubleHash, avatarId)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.DomainStorageError)
	}

	// generate tokens

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return a.signUpWithError(err, ec.Server, clientCode)
	}

	return dto.MakeSignUpOut(accessToken, refreshToken), nil
}

func (a *AuthService) RefreshTokens(ctx context.Context, refreshToken string) (
	dto.RefreshTokensOut, error,
) {
	payload, err := a.tokenManager.ParseRefresh(refreshToken) // with validation!
	if err != nil {

		// believe that module is working correctly!
		return a.refreshTokensWithError(err, ec.Client, ec.ValidateRefreshTokenFailed)
	}
	errForClient := a.identicalHashesForRefreshTokens(ctx, payload.UserId, refreshToken)
	if errForClient != nil {

		// is unpacking necessary?
		return a.refreshTokensWithError(
			errForClient.Unwrap(),
			errForClient.GuiltySide(),
			errForClient.ClientCode(),
		)
	}

	// generate tokens

	accessToken, refreshToken, err := a.generateTokens(payload.UserId)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, ec.TokenManagerError)
	}
	clientCode, err := a.updateHashRefreshToken(ctx, payload.UserId, refreshToken)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, clientCode)
	}

	return dto.MakeRefreshTokensOut(accessToken, refreshToken), nil
}

// -----------------------------------------------------------------------

func (a *AuthService) WasUserWithIdDeleted(ctx context.Context, id uint64) (bool, error) {

	// TODO: create cache? Try Redis?

	wasDeleted, err := a.domainStorage.WasUserDeleted(ctx, id)
	if err != nil {
		return false, utl.NewFuncError(a.WasUserWithIdDeleted,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return wasDeleted, nil
}

// error wrapper
// -----------------------------------------------------------------------

func (a *AuthService) signInWithError(err error, side, code int) (dto.SignInOut, error) {
	return dto.MakeSignInOutEmpty(),
		utl.NewFuncError(a.SignIn, ec.New(err, side, code))
}

func (a *AuthService) signUpWithError(err error, side, code int) (dto.SignUpOut, error) {
	return dto.MakeSignUpOutEmpty(),
		utl.NewFuncError(a.SignUp, ec.New(err, side, code))
}

func (a *AuthService) refreshTokensWithError(err error, side, code int) (dto.RefreshTokensOut, error) {
	return dto.MakeRefreshTokensOutEmpty(),
		utl.NewFuncError(a.RefreshTokens, ec.New(err, side, code))
}

// private
// -----------------------------------------------------------------------

func (a *AuthService) generateAndSaveAvatarForUser(ctx context.Context, login string) (uint64, error) {
	imageBytes, imageHash, err := a.generateAvatarForUser(login)
	if err != nil {
		return 0, utl.NewFuncError(a.generateAndSaveAvatarForUser, err)
	}

	// ***

	// TODO: combine into a transaction?
	avatarId, err := a.domainStorage.InsertServerGeneratedAvatar(ctx, imageHash)
	if err != nil {
		return 0, utl.NewFuncError(a.generateAndSaveAvatarForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	err = a.fileStorage.SaveImage(ctx, file.NewPngImageFromBytes(avatarId, imageBytes))
	if err != nil {
		_ = a.domainStorage.DeleteAvatarWithId(ctx, avatarId) // ignore err.

		return 0, utl.NewFuncError(a.generateAndSaveAvatarForUser,
			ec.New(err, ec.Server, ec.FileStorageError))
	}

	return avatarId, nil
}

func (a *AuthService) generateAvatarForUser(login string) ([]byte, string, error) {
	image, err := a.avatarGenerator.NewForUser(login)
	if err != nil {
		return nil, "", utl.NewFuncError(a.generateAvatarForUser,
			ec.New(err, ec.Server, ec.AvatarGeneratorError))
	}

	// ***

	imageBytes, err := utl.ImageToPngBytes(image)
	if err != nil {
		return nil, "", utl.NewFuncError(a.generateAvatarForUser,
			ec.New(err, ec.Server, ec.AvatarGeneratorError))
	}

	imageHash, err := a.hashManager.NewFromBytes(imageBytes)
	if err != nil {
		return nil, "", utl.NewFuncError(a.generateAvatarForUser,
			ec.New(err, ec.Server, ec.HashManagerError))
	}

	return imageBytes, imageHash, nil
}

// -----------------------------------------------------------------------

func (a *AuthService) generateTokens(userId uint64) (string, string, error) {
	access, err := a.tokenManager.New(
		token.MakePayload(userId, token.ForAccess), a.accessTokenTTL)
	if err != nil {
		return "", "", utl.NewFuncError(a.generateTokens, err) // or just return an error?
	}
	refresh, err := a.tokenManager.New(
		token.MakePayload(userId, token.ForRefresh), a.refreshTokenTTL)
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

func (a *AuthService) identicalHashesForRefreshTokens(ctx context.Context,
	userId uint64, refreshToken string) *ec.ErrorForClient {
	sourceFunc := a.identicalHashesForRefreshTokens

	currentHash, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return ec.NewErrorForClient(
			utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.HashManagerError,
		)
	}

	// there is no token for the deleted user!
	storageHash, err := a.domainStorage.GetHashRefreshToken(ctx, userId)
	if err != nil {
		return ec.NewErrorForClient(
			utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError,
		)
	}

	// ***

	if currentHash != storageHash { // client side...
		return ec.NewErrorForClient(
			ErrNotSameHashesForRefreshTokens,
			ec.Client, ec.InvalidRefreshToken,
		)
	}
	return nil
}

// calculator
// -----------------------------------------------------------------------

func (a *HasherAndStorages) passwordHashInHexToPasswordDoubleHash(val string) (string, error) {
	sourceFunc := a.passwordHashInHexToPasswordDoubleHash

	// believe that the module works correctly!

	passwordHash, err := hex.DecodeString(val)
	if err != nil {
		return "", utl.NewFuncError(sourceFunc,
			ec.New(err, ec.Client, ec.PasswordHashIsNotHex))
	}

	passwordDoubleHash, err := a.hashManager.NewFromBytes(passwordHash)
	if err != nil {
		return "", utl.NewFuncError(sourceFunc,
			ec.New(err, ec.Server, ec.HashManagerError))
	}

	return passwordDoubleHash, nil
}
