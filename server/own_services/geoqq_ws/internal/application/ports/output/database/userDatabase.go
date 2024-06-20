package database

import "context"

type UserDatabase interface {
	UpdateUserLocation(ctx context.Context, userId uint64, lon, lat float64) error
	HasUserWithId(ctx context.Context, userId uint64) (bool, error)
}
