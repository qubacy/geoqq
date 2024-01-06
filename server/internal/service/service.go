package service

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
)

type AuthService interface {
	SignIn(ctx context.Context, input dto.SignInInp) (dto.SignInOut, error)
	SignUp(ctx context.Context, input dto.SignUpInp) (dto.SignUpOut, error)
	RefreshTokens(ctx context.Context, refreshToken string) (dto.RefreshTokensOut, error)
}

// work with profiles, public users, ...
type UserService interface {
	GetProfileById(ctx context.Context, value uint64) (domain.Profile, error)
	GetUserById(ctx context.Context, value uint64) (domain.PublicUser, error)
	GetUsersByIds(ctx context.Context, values []uint64) (domain.PublicUserList, error)

	UpdateProfileById(ctx context.Context, input dto.UpdateProfileInp) error
}

type MateService interface {
	GetMateChatsForUser(ctx context.Context, userId uint64) (domain.MateChatList, error)
	DeleteMateChatById(ctx context.Context, value uint64) error

	GetMateChatMessagesByIdForUser(ctx context.Context,
		chatId, userId uint64,
		offset, count uint64) (domain.MateMessageList, error)
	GetMateRequestsForUser(ctx context.Context,
		userId uint64,
		offset, count uint64) (domain.MateChatList, error)
	GetMateRequestCountForUser(ctx context.Context, userId uint64) (uint64, error)

	AddMateRequest(ctx context.Context, userId uint64)
	UpdateMateRequestById()
}

type ImageService interface {
}

type GeoService interface {
}

// -----------------------------------------------------------------------

type Services interface {
	AuthService
	UserService
	MateService
	ImageService
	GeoService
}
