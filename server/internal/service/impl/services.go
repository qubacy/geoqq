package impl

import (
	"geoqq/internal/service"
	"geoqq/internal/storage"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"
)

type Services struct {
	authService service.AuthService
}

type Dependencies struct {
	TokenManager    token.TokenManager
	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration

	HashManager hash.HashManager

	Storage storage.Storage
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		authService: NewAuthService(deps),
	}, nil
}
