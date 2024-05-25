package database

import "context"

type UserDatabase interface {
	UpdateUserLocation(ctx context.Context,
		lon, lat float64, radius uint64) error
}
