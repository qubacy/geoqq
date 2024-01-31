package domain

import (
	"context"
	"geoqq/internal/domain"
)

// currently working with pictures!
type AvatarStorage interface {
	HasAvatar(ctx context.Context, id uint64) (bool, error)
	InsertGeneratedAvatar(ctx context.Context, hashValue string) (uint64, error)
}

type UserStorage interface {
	GetUserIdByByName(ctx context.Context, username string) (uint64, error)

	GetHashRefreshToken(ctx context.Context, id uint64) (string, error)

	HasUserWithName(ctx context.Context, value string) (bool, error)
	InsertUser(ctx context.Context,
		username, hashPassword string, avatarId uint64) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		username, hashPassword string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error

	UpdateHashRefreshToken(ctx context.Context, id uint64, value string) error
}

type UserProfileStorage interface {
	GetUserProfile(ctx context.Context, id uint64) (domain.UserProfile, error)
}

type MateRequestStorage interface {
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
	MateRequestStorage
	MateChatStorage
	GeoChatStorage
}
