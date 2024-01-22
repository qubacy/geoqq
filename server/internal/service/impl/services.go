package impl

import (
	domainStorage "geoqq/internal/storage/domain"
	"geoqq/pkg/avatar"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"
)

type Services struct {
	*AuthService
	*UserService
	*UserProfileService
}

type Dependencies struct {
	TokenManager    token.TokenManager
	HashManager     hash.HashManager
	AvatarGenerator avatar.AvatarGenerator
	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration
	Storage         domainStorage.Storage
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		AuthService:        newAuthService(deps),
		UserProfileService: newUserProfileService(deps),
	}, nil
}
