package database

import "context"

type UserDatabase interface {
	InsertUserLocation(ctx context.Context,
		lon, lat float64, radius uint64) error
}
