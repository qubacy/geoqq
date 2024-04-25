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

type GeneralParams struct {
	MaxPageSize uint64 // or uint32?
}

type AuthParams struct {
	SignIn SignInParams
	SignUp SignUpParams
}

type SignInParams struct {
	FailedAttemptCount uint32
	FailedAttemptTtl   time.Duration
	BlockingTime       time.Duration
}

type SignUpParams struct {
	BlockingTime time.Duration
}

type Dependencies struct {
	HashManager  hash.HashManager
	TokenManager token.TokenManager

	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration

	DomainStorage   domainStorage.Storage
	FileStorage     fileStorage.Storage
	AvatarGenerator avatar.AvatarGenerator

	GeoDistCalculator geoDistance.Calculator

	GeneralParams GeneralParams
	AuthParams    AuthParams
}

// -----------------------------------------------------------------------

type Services struct {
	*AuthService

	*UserProfileService
	*UserService

	*ImageService

	*MateRequestService
	*MateChatService
	*MateChatMessageService

	*GeoChatMessageService
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		AuthService: newAuthService(deps),

		UserProfileService: newUserProfileService(deps),
		UserService:        newUserService(deps),

		ImageService: newImageService(deps), // or avatar service?

		MateRequestService:     newMateRequestService(deps),
		MateChatService:        newMateChatService(deps),
		MateChatMessageService: newMateChatMessageService(deps),

		GeoChatMessageService: newGeoChatMessageService(deps),
	}, nil
}

// base?
// -----------------------------------------------------------------------

type HasherAndStorages struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
	hashManager   hash.HashManager
}
