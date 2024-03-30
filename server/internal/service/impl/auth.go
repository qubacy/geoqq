package impl

import (
	"context"
	"encoding/base64"
	"fmt"
	"geoqq/internal/service/dto"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/avatar"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	utl "geoqq/pkg/utility"
	"regexp"
	"time"
)

type AuthService struct {
	accessTokenTTL  time.Duration
	refreshTokenTTL time.Duration
	tokenManager    token.TokenManager
	hashManager     hash.HashManager
	avatarGenerator avatar.AvatarGenerator
	fileStorage     fileStorage.Storage
	domainStorage   domainStorage.Storage

	validators map[string]*regexp.Regexp
}

// without check?
func newAuthService(deps Dependencies) *AuthService {
	instance := &AuthService{
		accessTokenTTL:  deps.AccessTokenTTL,
		refreshTokenTTL: deps.RefreshTokenTTL,
		tokenManager:    deps.TokenManager,
		hashManager:     deps.HashManager,
		avatarGenerator: deps.AvatarGenerator,
		fileStorage:     deps.FileStorage,
		domainStorage:   deps.DomainStorage,
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
	err := a.validateSingIn(input)
	if err != nil {
		return a.signInWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}

	err = a.assertUserByCredentialsExists(ctx, input)
	if err != nil {
		return dto.SignInOut{}, utl.NewFuncError(a.SignIn, err)
	}

	userId, err := a.domainStorage.GetUserIdByByName(ctx, input.Login)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.DomainStorageError)
	}

	// ***

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
	err := a.validateSingUp(input)
	if err != nil {
		return a.signUpWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}
	err = a.assertUserWithNameNotExists(ctx, input)
	if err != nil {
		return dto.SignUpOut{}, utl.NewFuncError(a.SignUp, err)
	}

	// ***

	avatarId, err := a.generateAndSaveAvatarForUser(ctx, input.Login) // with error for client!
	if err != nil {
		return dto.MakeSignUpOutEmpty(), utl.NewFuncError(a.SignUp, err)
	}

	// ***

	passwordHash, err := base64.StdEncoding.DecodeString(input.PasswordHashInBase64)
	if err != nil {
		return a.signUpWithError(err, ec.Client, ec.PasswordHashIsNotBase64)
	}

	fmt.Println("passwordHash: ", passwordHash)

	passwordDoubleHash, err := a.hashManager.NewFromBytes(passwordHash) // <--- make hash hash-password!
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.HashManagerError)
	}

	fmt.Println("passwordDoubleHash: ", passwordDoubleHash)

	userId, err := a.domainStorage.InsertUser(ctx, input.Login, passwordDoubleHash, avatarId)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.DomainStorageError)
	}

	// *** tokens ***

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
	clientCode, err := a.identicalHashesForRefreshTokens(ctx, payload.UserId, refreshToken)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, clientCode) // or client?
	}

	// *** new tokens and hash ***

	accessToken, refreshToken, err := a.generateTokens(payload.UserId)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, ec.TokenManagerError)
	}
	clientCode, err = a.updateHashRefreshToken(ctx, payload.UserId, refreshToken)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, clientCode)
	}

	return dto.MakeRefreshTokensOut(accessToken, refreshToken), nil
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

// validators
// -----------------------------------------------------------------------

func (a *AuthService) initializeValidators() error {

	// can create global variables!
	sourceRegexp := map[string]string{
		"login":    "^[A-Za-z0-9_]{5,64}$",
		"password": `^[\s\S]+$`, // <--- checked on client!
	}

	// ***

	a.validators = make(map[string]*regexp.Regexp)
	for fieldName, sourceRe := range sourceRegexp {
		re, err := regexp.Compile(sourceRe)
		if err != nil {
			return utl.NewFuncError(a.initializeValidators, err)
		}

		a.validators[fieldName] = re
	}
	return nil
}

func (a *AuthService) validateLoginAndPassword(login, password string) error {
	loginValidator := a.validators["login"]
	base64Validator := a.validators["password"] // TODO: !!!

	// ***

	if len(loginValidator.String()) != 0 {
		if !loginValidator.MatchString(login) {
			return ErrIncorrectUsernameWithPattern(
				loginValidator.String())
		}
	}

	if len(base64Validator.String()) != 0 {
		if !base64Validator.MatchString(password) {
			return ErrIncorrectPassword // just an error with no func name!
		}
	}

	return nil
}

func (a *AuthService) validateSingUp(input dto.SignUpInp) error {
	return a.validateLoginAndPassword(
		input.Login, input.PasswordHashInBase64)
}

func (a *AuthService) validateSingIn(input dto.SignInInp) error {
	return a.validateLoginAndPassword(
		input.Login, input.PasswordHashInBase64)
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

	hashRefreshToken, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return ec.HashManagerError,
			utl.NewFuncError(a.updateHashRefreshToken, err)
	}

	err = a.domainStorage.UpdateHashRefreshTokenAndEntryTime(ctx, userId, hashRefreshToken)
	if err != nil {
		return ec.DomainStorageError,
			utl.NewFuncError(a.updateHashRefreshToken, err)
	}

	return ec.NoError, nil
}

func (a *AuthService) identicalHashesForRefreshTokens(ctx context.Context,
	userId uint64, refreshToken string) (int, error) {

	currentHash, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return ec.HashManagerError,
			utl.NewFuncError(a.identicalHashesForRefreshTokens, err)
	}
	storageHash, err := a.domainStorage.GetHashRefreshToken(ctx, userId)
	if err != nil {
		return ec.DomainStorageError,
			utl.NewFuncError(a.identicalHashesForRefreshTokens, err)
	}

	// ***

	if currentHash != storageHash { // client side...
		return ec.InvalidRefreshToken,
			ErrNotSameHashesForRefreshTokens
	}
	return ec.NoError, nil
}

// asserts
// -----------------------------------------------------------------------

// Returning a compound error!
func (a *AuthService) assertUserByCredentialsExists(
	ctx context.Context, input dto.SignInInp) error {

	passwordHash, err := base64.StdEncoding.DecodeString(input.PasswordHashInBase64)
	if err != nil {
		return utl.NewFuncError(a.assertUserByCredentialsExists,
			ec.New(err, ec.Client, ec.PasswordHashIsNotBase64))
	}

	fmt.Println("passwordHash: ", passwordHash)

	passwordDoubleHash, err := a.hashManager.NewFromBytes(passwordHash) // <--- make hash hash-password!
	if err != nil {
		return utl.NewFuncError(a.assertUserByCredentialsExists,
			ec.New(err, ec.Server, ec.HashManagerError))
	}

	fmt.Println("passwordDoubleHash: ", passwordDoubleHash)

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
