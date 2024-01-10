package storage

import (
	"context"
)

// currently working with pictures!
type ImageStorage interface {
}

type UserStorage interface {
	HasUserWithName(ctx context.Context, value string) (bool, error)
	InsertUser(ctx context.Context, username,
		hashPassword, hashUpdToken string) (uint64, error)

	HasUserByCredentials(ctx context.Context,
		username, hashPassword string) (bool, error)

	UpdateUserLocation(ctx context.Context, id uint64,
		longitude, latitude float64) error
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
	MateRequestStorage
	MateChatStorage
	GeoChatStorage
}
