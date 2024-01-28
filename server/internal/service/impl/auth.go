package impl

import (
	"context"
	"geoqq/internal/service/dto"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/avatar"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"geoqq/pkg/utility"
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

// -----------------------------------------------------------------------

func (a *AuthService) signInWithError(err error, side, code int) (dto.SignInOut, error) {
	return dto.MakeSignInOutEmpty(),
		utl.NewFuncError(a.SignIn, ec.New(err, side, code))
}

func (a *AuthService) SignIn(ctx context.Context, input dto.SignInInp) (
	dto.SignInOut, error,
) {
	err := a.validateSingIn(input)
	if err != nil {
		return a.signInWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}

	// ***

	hashPassword, err := a.hashManager.NewFromString(input.Password) // <--- hash hash password!
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.HashManagerError)
	}

	exists, err := a.domainStorage.HasUserByCredentials(ctx, input.Login, hashPassword)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return a.signInWithError(ErrIncorrectLoginOrPassword,
			ec.Client, ec.UserNotFound)
	}

	// ***

	userId, err := a.domainStorage.GetUserIdByByName(ctx, input.Login)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.DomainStorageError)
	}

	// ***

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.TokenManagerError)
	}
	err, clientCode := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return a.signInWithError(err, ec.Server, clientCode)
	}

	return dto.MakeSignInOut(accessToken, refreshToken), nil
}

// -----------------------------------------------------------------------

func (a *AuthService) signUpWithError(err error, side, code int) (dto.SignUpOut, error) {
	return dto.MakeSignUpOutEmpty(),
		utl.NewFuncError(a.SignUp, ec.New(err, side, code))
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (
	dto.SignUpOut, error,
) {
	err := a.validateSingUp(input)
	if err != nil {
		return a.signUpWithError(err, ec.Client, ec.ValidateInputParamsFailed)
	}

	// ***

	hashPassword, err := a.hashManager.NewFromString(input.Password) // <--- hash hash password!
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.HashManagerError)
	}

	exists, err := a.domainStorage.HasUserWithName(ctx, input.Login)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.DomainStorageError)
	}
	if exists {
		return a.signUpWithError(ErrUserWithThisLoginAlreadyExists,
			ec.Client, ec.UserAlreadyExist)
	}

	// ***

	image, err := a.avatarGenerator.NewForUser(input.Login)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.AvatarGeneratorError)
	}
	imageBytes, err := utility.ImageToPngBytes(image)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.AvatarGeneratorError)
	}

	imageHash, err := a.hashManager.NewFromBytes(imageBytes)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.AvatarGeneratorError)
	}

	avatarId, err := a.domainStorage.InsertGeneratedAvatar(ctx, imageHash)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.AvatarGeneratorError)
	}

	err = a.fileStorage.SaveImage(ctx, file.NewPngImageFromBytes(avatarId, imageBytes))
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.FileStorageError)
	}

	userId, err := a.domainStorage.InsertUser(ctx, input.Login, hashPassword, avatarId)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.DomainStorageError)
	}

	// *** tokens ***

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.TokenManagerError)
	}
	err, clientCode := a.updateHashRefreshToken(ctx, userId, refreshToken)
	if err != nil {
		return a.signUpWithError(err, ec.Server, clientCode)
	}

	return dto.MakeSignUpOut(accessToken, refreshToken), nil
}

// -----------------------------------------------------------------------

func (a *AuthService) refreshTokensWithError(err error, side, code int) (dto.RefreshTokensOut, error) {
	return dto.MakeRefreshTokensOutEmpty(),
		utl.NewFuncError(a.RefreshTokens, ec.New(err, side, code))
}

func (a *AuthService) RefreshTokens(ctx context.Context, refreshToken string) (
	dto.RefreshTokensOut, error,
) {
	payload, err := a.tokenManager.Parse(refreshToken) // with validation!
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, ec.DomainStorageError)
	}
	err, clientCode := a.identicalHashesForRefreshTokens(ctx, payload.UserId, refreshToken)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, clientCode)
	}

	// *** new tokens and hash ***

	accessToken, refreshToken, err := a.generateTokens(payload.UserId)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, ec.TokenManagerError)
	}
	err, clientCode = a.updateHashRefreshToken(ctx, payload.UserId, refreshToken)
	if err != nil {
		return a.refreshTokensWithError(err, ec.Server, clientCode)
	}

	return dto.MakeRefreshTokensOut(accessToken, refreshToken), nil
}

// validators
// -----------------------------------------------------------------------

func (a *AuthService) initializeValidators() error {

	// can create global variables!
	sourceRegexp := map[string]string{
		"login":    "^[A-Za-z0-9_]{5,64}$",
		"password": "^.+$", // <--- checked on client!
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
	passwordValidator := a.validators["password"]

	// ***

	if len(loginValidator.String()) != 0 {
		if !loginValidator.MatchString(login) {
			return ErrIncorrectUsernameWithPattern(
				loginValidator.String())
		}
	}
	if len(passwordValidator.String()) != 0 {
		if !passwordValidator.MatchString(password) {
			return ErrIncorrectPassword
		}
	}

	return nil
}

func (a *AuthService) validateSingUp(input dto.SignUpInp) error {
	return a.validateLoginAndPassword(input.Login, input.Password)
}

func (a *AuthService) validateSingIn(input dto.SignInInp) error {
	return a.validateLoginAndPassword(input.Login, input.Password)
}

// private
// -----------------------------------------------------------------------

func (a *AuthService) generateTokens(userId uint64) (string, string, error) {
	access, err := a.tokenManager.New(
		token.MakePayload(userId), a.accessTokenTTL)
	if err != nil {
		return "", "", err
	}
	refresh, err := a.tokenManager.New(
		token.MakePayload(userId), a.refreshTokenTTL)
	if err != nil {
		return "", "", err
	}

	return access, refresh, nil
}

func (a *AuthService) updateHashRefreshToken(ctx context.Context,
	userId uint64, refreshToken string) (error, int) { // <--- with client code!

	hashRefreshToken, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return err, ec.HashManagerError
	}

	err = a.domainStorage.UpdateHashRefreshToken(ctx, userId, hashRefreshToken)
	if err != nil {
		return err, ec.DomainStorageError
	}

	return nil, ec.NoError
}

func (a *AuthService) identicalHashesForRefreshTokens(ctx context.Context,
	userId uint64, refreshToken string) (error, int) {

	currentHash, err := a.hashManager.NewFromString(refreshToken)
	if err != nil {
		return err, ec.HashManagerError
	}
	storageHash, err := a.domainStorage.GetHashRefreshToken(ctx, userId)
	if err != nil {
		return err, ec.DomainStorageError
	}

	// ***

	if currentHash != storageHash {
		return ErrNotSameHashesForRefreshTokens, ec.InvalidRefreshToken
	}
	return nil, ec.NoError
}
