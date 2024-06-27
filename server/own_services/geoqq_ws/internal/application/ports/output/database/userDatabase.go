package database

import (
	"common/pkg/storage/geoqq/sql/postgre/table"
	"context"
	"geoqq_ws/internal/application/domain"
)

type UserDatabase interface {
	UpdateUserLocation(ctx context.Context, userId uint64, lon, lat float64) error
	HasUserWithId(ctx context.Context, userId uint64) (bool, error)

	GetUserLocation(ctx context.Context, userId uint64) (*domain.UserLocation, error)
	GetUserEntryById(ctx context.Context, userId uint64) (*table.UserEntry, error)
}
