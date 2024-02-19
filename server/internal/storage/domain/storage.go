package domain

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
	"geoqq/internal/storage/domain/dto"
)

// currently working with pictures!
type AvatarStorage interface {
	HasAvatar(ctx context.Context, id uint64) (bool, error)
	InsertGeneratedAvatar(ctx context.Context, hashValue string) (uint64, error)
	InsertAvatar(ctx context.Context, hashValue string) (uint64, error)
}

type UserStorage interface {
	GetUserIdByByName(ctx context.Context, username string) (uint64, error)

	GetHashRefreshToken(ctx context.Context, id uint64) (string, error)

	HasUserWithId(ctx context.Context, id uint64) (bool, error)
	HasUserWithName(ctx context.Context, value string) (bool, error)
	InsertUser(ctx context.Context,
		username, hashPassword string, avatarId uint64) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		username, hashPassword string) (bool, error)
	HasUserByIdAndHashPassword(ctx context.Context,
		id uint64, hashPassword string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error

	UpdateHashRefreshToken(ctx context.Context, id uint64, value string) error

	UpdateUserParts(ctx context.Context, id uint64,
		input dto.UpdateUserPartsInp) error
}

type UserProfileStorage interface {
	GetUserProfile(ctx context.Context, id uint64) (domain.UserProfile, error)
}

type MateStorage interface {
	AreMates(ctx context.Context,
		firstUserId uint64, secondUserId uint64) (bool, error)
}

type MateRequestStorage interface {
	AddMateRequest(ctx context.Context,
		fromUserId, toUserId uint64) (uint64, error)
	HasWaitingMateRequest(ctx context.Context,
		fromUserId, toUserId uint64) (bool, error)

	IsMateRequestForUser(ctx context.Context, id, userId uint64) (bool, error)
	HasMateRequestByIdAndToUser(ctx context.Context, id, toUserId uint64) (bool, error)

	GetMateRequestResultById(ctx context.Context, id uint64) (table.MateRequestResult, error)
	UpdateMateRequestResultById(ctx context.Context, id uint64,
		value table.MateRequestResult) error
}

type MateChatStorage interface {
}

type GeoChatStorage interface {
}

// -----------------------------------------------------------------------

type Storage interface {
	AvatarStorage
	UserStorage
	UserProfileStorage
	MateStorage
	MateRequestStorage
	MateChatStorage
	GeoChatStorage
}
