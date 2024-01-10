package impl

import (
	"context"
	"geoqq/internal/service/dto"
	"geoqq/internal/storage"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"geoqq/pkg/utility"
	"time"
)

type AuthService struct {
	accessTokenTTL  time.Duration
	refreshTokenTTL time.Duration
	tokenManager    token.TokenManager
	hashManager     hash.HashManager
	storage         storage.Storage
}

// without check?
func newAuthService(deps Dependencies) *AuthService {
	return &AuthService{
		accessTokenTTL:  deps.AccessTokenTTL,
		refreshTokenTTL: deps.RefreshTokenTTL,
		tokenManager:    deps.TokenManager,
		hashManager:     deps.HashManager,
		storage:         deps.Storage,
	}
}

// -----------------------------------------------------------------------

func (a *AuthService) SignIn(ctx context.Context, input dto.SignInInp) (dto.SignInOut, error) {

	return dto.SignInOut{}, nil
}

func (a *AuthService) SignUp(ctx context.Context, input dto.SignUpInp) (dto.SignUpOut, error) {
	hashPassword, err := a.hashManager.New(input.Password)
	if err != nil {
		return dto.SignUpOut{}, utility.CreateCustomError(a.SignIn, err)
	}

	a.storage.InsertUser(ctx, input.Login, hashPassword, "")

	return dto.SignUpOut{}, nil
}

func (a *AuthService) RefreshTokens(ctx context.Context, refreshToken string) (dto.RefreshTokensOut, error) {
	return dto.RefreshTokensOut{}, nil
}
