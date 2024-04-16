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

	WasUserWithIdDeleted(ctx context.Context, id uint64) (bool, error)
}

// -----------------------------------------------------------------------

type UserProfileService interface {
	GetUserProfile(ctx context.Context, userId uint64) (domain.UserProfile, error)
	UpdateUserProfileWithAvatar(ctx context.Context,
		userId uint64, input dto.ProfileWithAvatarForUpdateInp) error

	UpdateUserProfile(ctx context.Context,
		userId uint64, input dto.ProfileForUpdateInp) error
	DeleteUserProfile(ctx context.Context, userId uint64) error
}

type PublicUserService interface {
	GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (*domain.PublicUser, error)
	GetPublicUserByIds(ctx context.Context, userId uint64, targetUserIds []uint64) (domain.PublicUserList, error)
}

// -----------------------------------------------------------------------

type MateChatService interface {
	GetMateChat(ctx context.Context, chatId, userId uint64) (*domain.MateChat, error)
	GetMateChatsForUser(ctx context.Context, userId, offset, count uint64) (domain.MateChatList, error)
	DeleteMateChatForUser(ctx context.Context, chatId, userId uint64) error
}

type MateChatMessageService interface {
	ReadMateChatMessagesByChatId(ctx context.Context,
		userId, chatId uint64,
		offset, count uint64) (domain.MateMessageList, error)
	AddMessageToMateChat(ctx context.Context, userId, chatId uint64, text string) error
}

type MateRequestService interface {
	GetAllIncomingMateRequestsForUser(ctx context.Context, userId uint64) (*dto.MateRequestsForUserOut, error)
	GetIncomingMateRequestsForUser(ctx context.Context, userId, offset, count uint64) (*dto.MateRequestsForUserOut, error)
	GetIncomingMateRequestCountForUser(ctx context.Context, userId uint64) (int, error)

	AddMateRequest(ctx context.Context, sourceUserId, targetUserId uint64) error
	SetResultForMateRequest(ctx context.Context, userId, mateRequestId uint64,
		mateRequestResult table.MateRequestResult) error
}

// -----------------------------------------------------------------------

type GeoChatMessageService interface {
	AddMessageToGeoChat(ctx context.Context, userId uint64,
		text string, longitude, latitude float64) error

	GetGeoChatAllMessages(ctx context.Context, distance uint64,
		latitude, longitude float64) (domain.GeoMessageList, error)
	GetGeoChatMessages(ctx context.Context, distance uint64,
		latitude, longitude float64, offset, count uint64) (domain.GeoMessageList, error)
}

// -----------------------------------------------------------------------

type ImageService interface {
	GetImageById(ctx context.Context, imageId uint64) (*file.Image, error)
	GetImagesByIds(ctx context.Context, imageIds []uint64) (*file.Images, error)

	AddImageToUser(ctx context.Context, userId uint64,
		input dto.ImageForAddToUserInp) (uint64, error)
}

// -----------------------------------------------------------------------

type Services interface {
	AuthService
	UserProfileService

	PublicUserService

	MateRequestService
	MateChatService
	MateChatMessageService

	GeoChatMessageService

	ImageService
}
