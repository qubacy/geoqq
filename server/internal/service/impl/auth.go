package impl

import (
	"context"
	"geoqq/internal/service/dto"
	"geoqq/internal/storage"
	"geoqq/pkg/hash"
	se "geoqq/pkg/sideError/impl"
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

func (a *AuthService) SignIn(ctx context.Context, input dto.SignInInp) (
	dto.SignInOut, error,
) {

	return dto.SignInOut{}, nil
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (
	dto.SignUpOut, error,
) {
	err := a.validateSingUp(input)
	if err != nil {
		return dto.MakeSignUpOutEmpty(),
			utl.NewFuncError(a.SignUp, se.New(err, se.Client)) // <--- guilty side!
	}

	// ***

	hashPassword, err := a.hashManager.New(input.Password) // <--- hash hash password!
	if err != nil {
		return dto.MakeSignUpOutEmpty(),
			utl.NewFuncError(a.SignUp, se.New(err, se.Server))
	}

	userId, err := a.storage.InsertUser(ctx, input.Login, hashPassword)
	if err != nil {
		return dto.MakeSignUpOutEmpty(),
			utl.NewFuncError(a.SignUp, se.New(err, se.Server))
	}

	// ***

	accessToken, refreshToken, err := a.generateTokens(userId)
	if err != nil {
		return dto.MakeSignUpOutEmpty(),
			utl.NewFuncError(a.SignUp, se.New(err, se.Server))
	}

	return dto.MakeSignUpOut(accessToken, refreshToken), nil
}

func (a *AuthService) RefreshTokens(ctx context.Context, refreshToken string) (
	dto.RefreshTokensOut, error,
) {
	return dto.RefreshTokensOut{}, nil
}

// validators
// -----------------------------------------------------------------------

func (a *AuthService) initializeValidators() error {
	// can create global variables!
	sourceRegexp := map[string]string{
		"login":    "[A-Za-z0-9_]{5,64}",
		"password": "", // <--- checked on client!
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

func (a *AuthService) validateSingUp(input dto.SignUpInp) error {
	loginValidator := a.validators["login"]

	if !loginValidator.Match([]byte(input.Login)) {
		return ErrIncorrectUsernameWithPattern(
			loginValidator.String())
	}
	if len(input.Password) == 0 {
		return ErrIncorrectPassword
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func (a *AuthService) generateTokens(userId uint64) (string, string, error) {
	access, err := a.tokenManager.New(token.MakePayload(userId), a.accessTokenTTL)
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
