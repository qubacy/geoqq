package impl

import (
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/avatar"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"
)

type Services struct {
	*AuthService
	*UserProfileService
	*ImageService
	*MateRequestService
	*UserService
}

type Dependencies struct {
	TokenManager    token.TokenManager
	HashManager     hash.HashManager
	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration
	AvatarGenerator avatar.AvatarGenerator
	DomainStorage   domainStorage.Storage
	FileStorage     fileStorage.Storage
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		AuthService:        newAuthService(deps),
		UserProfileService: newUserProfileService(deps),
		ImageService:       newImageService(deps),
		MateRequestService: newMateRequestService(deps),
		UserService:        newUserService(deps),
	}, nil
}
