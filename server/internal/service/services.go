package service

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
	"geoqq/internal/service/dto"
	"geoqq/pkg/file"
)

type AuthService interface {
	SignIn(ctx context.Context, input dto.SignInInp) (dto.SignInOut, error)
	SignUp(ctx context.Context, input dto.SignUpInp) (dto.SignUpOut, error)
	RefreshTokens(ctx context.Context, refreshToken string) (dto.RefreshTokensOut, error)
}

type UserProfileService interface {
	GetUserProfile(ctx context.Context, userId uint64) (domain.UserProfile, error)
	UpdateUserProfile(ctx context.Context, userId uint64, input dto.UpdateProfileInp) error
}

type PublicUserService interface {
	GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (*domain.PublicUser, error)
	GetPublicUserByIds(ctx context.Context, userId uint64, targetUserIds []uint64) ([]*domain.PublicUser, error)
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

	// UpdateMateRequest(ctx context.Context,
	// 	userId, requestId uint64, accepted bool) error
}

type MateChatService interface {
	AddMessageToMateChat(ctx context.Context, userId, chatId uint64, text string) error
	GetMateChatsForUser(ctx context.Context, userId, offset, count uint64) (domain.MateChatList, error)
}

type MateRequestService interface {
	GetAllIncomingMateRequestsForUser(ctx context.Context, userId uint64) (*dto.MateRequestsForUserOut, error)
	GetIncomingMateRequestsForUser(ctx context.Context, userId, offset, count uint64) (*dto.MateRequestsForUserOut, error)
	GetIncomingMateRequestCountForUser(ctx context.Context, userId uint64) (int, error)

	AddMateRequest(ctx context.Context, sourceUserId, targetUserId uint64) error
	SetResultForMateRequest(ctx context.Context, userId, mateRequestId uint64,
		mateRequestResult table.MateRequestResult) error
}

type ImageService interface {
	GetImageById(ctx context.Context, imageId uint64) (*file.Image, error)
	GetImagesByIds(ctx context.Context, imageIds []uint64) (*file.Images, error)
}

type GeoService interface {
}

// -----------------------------------------------------------------------

type Services interface {
	AuthService
	UserProfileService
	PublicUserService
	MateRequestService
	MateChatService
	ImageService
	GeoService
}
