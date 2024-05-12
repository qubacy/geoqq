package impl

import (
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/avatar"
	"geoqq/pkg/cache"
	"geoqq/pkg/geoDistance"
	"geoqq/pkg/hash"
	"geoqq/pkg/token"
	"time"

	utl "geoqq/pkg/utility"
)

type GeneralParams struct {
	MaxPageSize uint64 // or uint32?
}

type AuthParams struct {
	SignIn SignInParams
	SignUp SignUpParams

	LoginPattern    string
	PasswordPattern string
}

type SignInParams struct {
	FailedAttemptCount uint64
	FailedAttemptTtl   time.Duration
	BlockingTime       time.Duration
}

type SignUpParams struct {
	BlockingTime time.Duration
}

type ImageParams struct {
	CacheTtl       time.Duration
	AddImageParams AddImageParams
}

type AddImageParams struct {
	BlockingTime time.Duration
}

type ChatParams struct {
	MaxMessageLength uint64
	GeoChatParams    GeoChatParams
}

type GeoChatParams struct {
	MaxMessageCountReturned uint64
	MaxRadius               uint64
	MinRadius               uint64
}

type UserParams struct {
	NamePattern          string
	UpdateUsernameParams UpdateUsernameParams
}

type UpdateUsernameParams struct {
	BlockingTime time.Duration
}

type Dependencies struct {
	HashManager  hash.HashManager
	TokenManager token.TokenManager

	AccessTokenTTL  time.Duration
	RefreshTokenTTL time.Duration

	EnableCache bool
	Cache       cache.Cache

	DomainStorage   domainStorage.Storage
	FileStorage     fileStorage.Storage
	AvatarGenerator avatar.AvatarGenerator

	GeoDistCalculator geoDistance.Calculator

	GeneralParams GeneralParams
	AuthParams    AuthParams
	ImageParams   ImageParams
	UserParams    UserParams
	ChatParams    ChatParams
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
	authService, err := newAuthService(deps)
	if err != nil {
		return nil, utl.NewFuncError(NewServices, err)
	}

	userProfileService, err := newUserProfileService(deps)
	if err != nil {
		return nil, utl.NewFuncError(NewServices, err)
	}

	// ***

	return &Services{
		AuthService: authService,

		UserProfileService: userProfileService,
		UserService:        newUserService(deps),

		ImageService: newImageService(deps), // or avatar service?

		MateRequestService:     newMateRequestService(deps),
		MateChatService:        newMateChatService(deps),
		MateChatMessageService: newMateChatMessageService(deps),

		GeoChatMessageService: newGeoChatMessageService(deps),
	}, nil
}
