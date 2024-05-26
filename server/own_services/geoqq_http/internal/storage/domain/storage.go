package domain

import (
	"context"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/domain/table"
	"geoqq_http/internal/storage/domain/dto"
)

// avatar == image

const (
	LabelDeletedUser = "deletedUser"
)

type AvatarStorage interface {
	HasAvatar(ctx context.Context, id uint64) (bool, error)
	HasAvatars(ctx context.Context, uniqueIds []uint64) (bool, error)
	HasAvatarsWithLabel(ctx context.Context, label string) (bool, error)

	GetAvatarIdsByLabel(ctx context.Context, label string) ([]uint64, error)
	GetRandomAvatarIdByLabel(ctx context.Context, label string) (uint64, error)

	InsertServerGeneratedAvatar(ctx context.Context, hashValue string) (uint64, error)
	InsertServerGeneratedAvatarWithLabel(ctx context.Context,
		hashValue, label string) (uint64, error)

	InsertAvatar(ctx context.Context, userId uint64, hashValue string) (uint64, error)
	DeleteAvatarWithId(ctx context.Context, id uint64) error
}

// -----------------------------------------------------------------------

type UserStorage interface {
	GetUserIdByByLogin(ctx context.Context, login string) (uint64, error)
	GetHashRefreshToken(ctx context.Context, id uint64) (string, error)
	GetUserOptionsById(ctx context.Context, id uint64) (*table.UserOptions, error)

	HasUserWithId(ctx context.Context, id uint64) (bool, error)
	HasUserWithIds(ctx context.Context, uniqueIds []uint64) (bool, error)

	HasUserWithLogin(ctx context.Context, login string) (bool, error)
	InsertUser(ctx context.Context,
		login, passwordDoubleHash string,
		avatarId uint64) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		login, passwordDoubleHash string) (bool, error)
	HasUserByIdAndHashPassword(ctx context.Context,
		id uint64, passwordDoubleHash string) (bool, error)

	WasUserDeleted(ctx context.Context, id uint64) (bool, error)
	WasUserWithLoginDeleted(ctx context.Context, login string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error

	ResetHashRefreshToken(ctx context.Context, id uint64) error
	UpdateHashRefreshTokenAndSomeTimes(ctx context.Context,
		id uint64, value string) error

	UpdateUserParts(ctx context.Context, id uint64,
		input *dto.UpdateUserPartsInp) error

	// ***

	UpdateLastActivityTimeForUser(ctx context.Context, id uint64) error
}

type PublicUserTransform = func(*domain.PublicUser)

type PublicUserStorage interface {
	GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (*domain.PublicUser, error)
	GetPublicUsersByIds(ctx context.Context, userId uint64, targetUserIds []uint64) (domain.PublicUserList, error)

	// ***

	GetTransformedPublicUserById(ctx context.Context,
		userId uint64, targetUserId uint64,
		transform PublicUserTransform,
	) (*domain.PublicUser, error)
	GetTransformedPublicUsersByIds(ctx context.Context,
		userId uint64, targetUserIds []uint64,
		transform PublicUserTransform,
	) (domain.PublicUserList, error)
}

type UserProfileStorage interface {
	GetUserProfile(ctx context.Context, userId uint64) (*domain.UserProfile, error)
	DeleteUserProfile(ctx context.Context, userId uint64) error
}

type MateStorage interface {
	AreMates(ctx context.Context,
		firstUserId uint64, secondUserId uint64) (bool, error)
	InsertMate(ctx context.Context,
		firstUserId uint64, secondUserId uint64) (uint64, error)
}

// -----------------------------------------------------------------------

type MateRequestStorage interface {
	AddMateRequest(ctx context.Context,
		fromUserId, toUserId uint64) (uint64, error)
	HasWaitingMateRequest(ctx context.Context,
		fromUserId, toUserId uint64) (bool, error)

	IsMateRequestForUser(ctx context.Context, id, userId uint64) (bool, error)
	HasMateRequestByIdAndToUser(ctx context.Context, id, toUserId uint64) (bool, error)

	GetMateRequestById(ctx context.Context, id uint64) (*table.MateRequest, error)
	GetMateRequestResultById(ctx context.Context, id uint64) (table.MateRequestResult, error)
	UpdateMateRequestResultById(ctx context.Context, id uint64,
		value table.MateRequestResult) error

	AcceptMateRequestById(ctx context.Context, id,
		firstUserId, secondUserId uint64) error
	AcceptMateRequestByIdWithReturningMateChatId(ctx context.Context, id,
		firstUserId, secondUserId uint64) (uint64, error)
	RejectMateRequestById(ctx context.Context, id, firstUserId, secondUserId uint64) error

	GetAllWaitingMateRequestsForUser(ctx context.Context, userId uint64) ([]*table.MateRequest, error)
	GetWaitingMateRequestsForUser(ctx context.Context, userId, offset, count uint64) ([]*table.MateRequest, error)
	GetWaitingMateRequestCountForUser(ctx context.Context, userId uint64) (int, error)
}

type MateChatStorage interface {
	AvailableMateChatWithIdForUser(ctx context.Context, chatId, userId uint64) (bool, error)

	HasMateChatWithId(ctx context.Context, id uint64) (bool, error)
	InsertMateChat(ctx context.Context, firstUserId uint64, secondUserId uint64) (uint64, error)

	GetMateChatsForUser(ctx context.Context, userId, offset, count uint64) ([]*domain.MateChat, error)
	GetMateChatWithIdForUser(ctx context.Context, userId, chatId uint64) (*domain.MateChat, error)

	DeleteMateChatForUser(ctx context.Context, userId, chatId uint64) error

	// ***

	GetTableMateChatWithId(ctx context.Context, id uint64) (*table.MateChat, error)
	GetTableMateChatWithIdForUser(ctx context.Context, chatId, userId uint64) (*table.MateChat, error)
}

type MateChatMessageStorage interface {
	InsertMateChatMessage(ctx context.Context, chatTd, fromUserId uint64, text string) (uint64, error)
	GetMateChatMessagesByChatId(ctx context.Context,
		userId, chatId uint64,
		count, offset uint64) (domain.MateMessageList, error)

	// Make messages read!
	ReadMateChatMessagesByChatId(ctx context.Context,
		userId, chatId uint64,
		count, offset uint64) (domain.MateMessageList, error)
}

// -----------------------------------------------------------------------

type GeoChatMessageStorage interface {
	InsertGeoChatMessage(ctx context.Context,
		fromUserId uint64, text string,
		latitude, longitude float64) (uint64, error)
	InsertGeoChatMessageWithUpdateUserLocation(ctx context.Context,
		fromUserId uint64, text string,
		latitude, longitude float64) (uint64, error)

	// ***

	GetGeoChatAllMessages(ctx context.Context, distance uint64,
		latitude, longitude float64) (domain.GeoMessageList, error)

	GetGeoChatMessages(ctx context.Context, distance uint64,
		latitude, longitude float64,
		offset, count uint64) (domain.GeoMessageList, error)
}

// -----------------------------------------------------------------------

type Storage interface {
	AvatarStorage

	UserStorage
	PublicUserStorage
	UserProfileStorage

	MateStorage
	MateRequestStorage
	MateChatStorage
	MateChatMessageStorage

	GeoChatMessageStorage

	Background
}
