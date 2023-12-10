package storage

import (
	"context"
	"geoqq/internal/domain"
)

type ResourceStorage interface {
}

type UserStorage interface {
	SelectUser(ctx context.Context, id uint64) (domain.User, error)
	SelectUserEntry(ctx context.Context, id uint64) (domain.UserEntry, error)
	SelectUserLocation(ctx context.Context, id uint64) (domain.UserLocation, error)
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
