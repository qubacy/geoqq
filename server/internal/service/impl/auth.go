package impl

import (
	"context"
	"geoqq/internal/service/dto"
	"geoqq/internal/storage"
	ec "geoqq/pkg/errorForClient/impl"
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
	storage         storage.Storage

	validators map[string]*regexp.Regexp
}

// without check?
func newAuthService(deps Dependencies) *AuthService {
	instance := &AuthService{
		accessTokenTTL:  deps.AccessTokenTTL,
		refreshTokenTTL: deps.RefreshTokenTTL,
		tokenManager:    deps.TokenManager,
		hashManager:     deps.HashManager,
		storage:         deps.Storage,
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
		return a.signInWithError(err, ec.Client, ec.InvalidInputParams) // <--- without details!
	}

	// ***

	hashPassword, err := a.hashManager.New(input.Password) // <--- hash hash password!
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.HashManagerError)
	}

	exists, err := a.storage.HasUserByCredentials(ctx, input.Login, hashPassword)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.StorageError)
	}
	if !exists {
		return a.signInWithError(ErrIncorrectLoginOrPassword,
			ec.Client, ec.UserNotFound)
	}

	// ***

	userId, err := a.storage.GetUserIdByByName(ctx, input.Login)
	if err != nil {
		return a.signInWithError(err, ec.Server, ec.StorageError)
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

func (a *AuthService) signUpWithError(err error, side, code int) (dto.SignUpOut, error) {
	return dto.MakeSignUpOutEmpty(),
		utl.NewFuncError(a.SignUp, ec.New(err, side, code))
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (
	dto.SignUpOut, error,
) {
	err := a.validateSingUp(input)
	if err != nil {
		return a.signUpWithError(err, ec.Client, ec.InvalidInputParams)
	}

	// ***

	hashPassword, err := a.hashManager.New(input.Password) // <--- hash hash password!
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.HashManagerError)
	}

	exists, err := a.storage.HasUserWithName(ctx, input.Login)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.StorageError)
	}
	if exists {
		return a.signUpWithError(ErrUserWithThisLoginAlreadyExists,
			ec.Client, ec.UserAlreadyExist)
	}

	userId, err := a.storage.InsertUser(ctx, input.Login, hashPassword)
	if err != nil {
		return a.signUpWithError(err, ec.Server, ec.StorageError)
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
		return a.refreshTokensWithError(err, ec.Server, ec.StorageError)
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

	hashRefreshToken, err := a.hashManager.New(refreshToken)
	if err != nil {
		return err, ec.HashManagerError
	}

	err = a.storage.UpdateHashRefreshToken(ctx, userId, hashRefreshToken)
	if err != nil {
		return err, ec.StorageError
	}

	return nil, ec.NoError
}

func (a *AuthService) identicalHashesForRefreshTokens(ctx context.Context,
	userId uint64, refreshToken string) (error, int) {

	currentHash, err := a.hashManager.New(refreshToken)
	if err != nil {
		return err, ec.HashManagerError
	}
	storageHash, err := a.storage.GetHashRefreshToken(ctx, userId)
	if err != nil {
		return err, ec.StorageError
	}

	// ***

	if currentHash != storageHash {
		return ErrNotSameHashesForRefreshTokens, ec.InvalidRefreshToken
	}
	return nil, ec.NoError
}
