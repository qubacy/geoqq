package impl

import (
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/avatar"
	"geoqq/pkg/geoDistance"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"
)

type Services struct {
	*AuthService
	*UserProfileService
	*UserService
	*MateRequestService
	*MateChatService
	*MateChatMessageService
	*GeoChatMessageService
	*ImageService
}

type Dependencies struct {
	TokenManager token.TokenManager
	HashManager  hash.HashManager

	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration

	DomainStorage   domainStorage.Storage
	FileStorage     fileStorage.Storage
	AvatarGenerator avatar.AvatarGenerator

	GeoDistanceCalculator geoDistance.Calculator
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		AuthService:            newAuthService(deps),
		UserProfileService:     newUserProfileService(deps),
		ImageService:           newImageService(deps),
		MateRequestService:     newMateRequestService(deps),
		MateChatService:        newMateChatService(deps),
		MateChatMessageService: newMateChatMessageService(deps),
		GeoChatMessageService:  newGeoChatMessageService(deps),
		UserService:            newUserService(deps),
	}, nil
}
