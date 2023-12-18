package storage

import (
	"context"
	"geoqq/internal/domain"
)

type ResourceStorage interface {
}

type UserStorage interface {
	InsertUser(ctx context.Context, username,
		hashPassword, hashUpdToken string) (uint64, error)

	HasUserByCredentials(ctx context.Context, username, hashPassword string) (bool, error)
	SelectUserByUsername(ctx context.Context, username string) (domain.User, error)

	SelectUser(ctx context.Context, id uint64) (domain.User, error)
	SelectUsers(ctx context.Context, ids []uint64) (domain.Users, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error
}

type MateStorage interface {
}

type MateChatStorage interface {
}

type GeoChatStorage interface {
}

// -----------------------------------------------------------------------

type Storage interface {
	ResourceStorage
	UserStorage
	MateStorage
	MateChatStorage
	GeoChatStorage
}
