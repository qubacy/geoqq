package domain

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
	"geoqq/internal/storage/domain/dto"
)

// avatar == image

type AvatarStorage interface {
	HasAvatar(ctx context.Context, id uint64) (bool, error)
	HasAvatars(ctx context.Context, uniqueIds []uint64) (bool, error)

	InsertServerGeneratedAvatar(ctx context.Context, hashValue string) (uint64, error)
	InsertServerGeneratedAvatarWithLabel(ctx context.Context,
		hashValue, label string) (uint64, error)

	InsertAvatar(ctx context.Context, userId uint64, hashValue string) (uint64, error)
	DeleteAvatarWithId(ctx context.Context, id uint64) error
}

// -----------------------------------------------------------------------

type UserStorage interface {
	GetUserIdByByName(ctx context.Context, username string) (uint64, error)
	GetHashRefreshToken(ctx context.Context, id uint64) (string, error)

	HasUserWithId(ctx context.Context, id uint64) (bool, error)
	HasUserWithIds(ctx context.Context, uniqueIds []uint64) (bool, error)

	HasUserWithName(ctx context.Context, value string) (bool, error)
	InsertUser(ctx context.Context,
		username, passwordDoubleHash string,
		avatarId uint64) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		username, passwordDoubleHash string) (bool, error)
	HasUserByIdAndHashPassword(ctx context.Context,
		id uint64, passwordDoubleHash string) (bool, error)

	WasUserDeleted(ctx context.Context, id uint64) (bool, error)
	WasUserWithNameDeleted(ctx context.Context, username string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error

	ResetHashRefreshToken(ctx context.Context, id uint64) error
	UpdateHashRefreshTokenAndSomeTimes(ctx context.Context,
		id uint64, value string) error

	UpdateUserParts(ctx context.Context, id uint64,
		input dto.UpdateUserPartsInp) error

	// ***

	UpdateLastActivityTimeForUser(ctx context.Context, id uint64) error
}

type PublicUserStorage interface {
	GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (*domain.PublicUser, error)
	GetPublicUsersByIds(ctx context.Context, userId uint64, targetUserIds []uint64) (domain.PublicUserList, error)
}

type UserProfileStorage interface {
	GetUserProfile(ctx context.Context, userId uint64) (domain.UserProfile, error)
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

	AcceptMateRequestById(ctx context.Context, id, firstUserId, secondUserId uint64) error
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
