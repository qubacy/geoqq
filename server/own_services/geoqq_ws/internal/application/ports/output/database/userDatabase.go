package database

import (
	domain "common/pkg/domain/geoqq"
	"common/pkg/storage/geoqq/sql/postgre/table"
	"context"
)

// TODO: slight duplication as in the service `geoqq http`

type PublicUserTransform = func(*domain.PublicUser)

type UserDatabase interface {
	UpdateUserLocation(ctx context.Context, userId uint64, lon, lat float64) error
	HasUserWithId(ctx context.Context, userId uint64) (bool, error)

	GetUserLocation(ctx context.Context, userId uint64) (*domain.UserLocation, error)
	GetUserEntryById(ctx context.Context, userId uint64) (*table.UserEntry, error)

	GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (*domain.PublicUser, error)
	GetTransformedPublicUserById(ctx context.Context,
		userId uint64, targetUserId uint64,
		transform PublicUserTransform,
	) (*domain.PublicUser, error)
}
