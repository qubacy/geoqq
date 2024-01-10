package service

import (
	"context"
	"geoqq/internal/service/dto"
)

type AuthService interface {
	SignIn(ctx context.Context, input dto.SignInInp) (dto.SignInOut, error)
	SignUp(ctx context.Context, input dto.SignUpInp) (dto.SignUpOut, error)
	RefreshTokens(ctx context.Context, refreshToken string) (dto.RefreshTokensOut, error)
}

// TODO: need to split?
// work with profiles, public users, ...
type UserService interface {

	// // dangerous methods!
	// // TODO: need to check who is near user?
	// GetProfileById(ctx context.Context, srcUserId uint64) (domain.Profile, error)
	// GetUserById(ctx context.Context, srcUserId, targetUserId uint64) (domain.PublicUser, error)
	// GetUsersByIds(ctx context.Context, values []uint64) (domain.PublicUserList, error)

	// UpdateProfileById(ctx context.Context, input dto.UpdateProfileInp) error
}

// TODO: need to split?
// work with mate chats, mate requests, ...
type MateService interface {
	// GetMateChats(ctx context.Context, userId uint64) (domain.MateChatList, error)
	// DeleteMateChatById(ctx context.Context, value uint64) error

	// GetMessagesByMateChatId(ctx context.Context,
	// 	chatId, userId uint64,
	// 	offset, count uint64) (domain.MateMessageList, error)
	// GetMateRequests(ctx context.Context,
	// 	userId uint64,
	// 	offset, count uint64) (domain.MateRequestList, error)
	// GetMateRequestCount(ctx context.Context, userId uint64) (uint64, error)

	// AddMateRequest(ctx context.Context, sourceUserId, targetUserId uint64) error
	// UpdateMateRequest(ctx context.Context,
	// 	userId, requestId uint64, accepted bool) error
}

type ImageService interface {
	// GetImageById(ctx context.Context, imageId, userId uint64) (table.Image, error)
	// GetImagesByIds(ctx context.Context, imageIds []uint64, userId uint64) ([]table.Image, error)
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
