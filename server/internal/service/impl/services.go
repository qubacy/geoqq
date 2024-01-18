package impl

import (
	"geoqq/internal/storage"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"
)

type Services struct {
	*AuthService
	*UserService
	*ProfileService
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
		AuthService:    newAuthService(deps),
		ProfileService: newProfileService(deps),
	}, nil
}
