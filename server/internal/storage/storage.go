package storage

import (
	"context"
	"geoqq/internal/domain"
)

// currently working with pictures!
type ImageStorage interface {
}

type UserStorage interface {
	GetUserIdByByName(ctx context.Context, username string) (uint64, error)

	GetHashRefreshToken(ctx context.Context, id uint64) (string, error)

	HasUserWithName(ctx context.Context, value string) (bool, error)
	InsertUser(ctx context.Context, username, hashPassword string) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		username, hashPassword string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error

	UpdateHashRefreshToken(ctx context.Context, id uint64, value string) error
}

type ProfileStorage interface {
	GetProfileById(ctx context.Context, id uint64) (domain.Profile, error)
}

type MateRequestStorage interface {
}

type MateChatStorage interface {
}

type GeoChatStorage interface {
}

// -----------------------------------------------------------------------

type Storage interface {
	ImageStorage
	UserStorage
	ProfileStorage
	MateRequestStorage
	MateChatStorage
	GeoChatStorage
}
