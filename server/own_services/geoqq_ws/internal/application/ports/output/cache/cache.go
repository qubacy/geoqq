package cache

import "context"

type Location struct {
	Lat float64
	Lon float64
}

type Cache interface {
	AddUserLocation(ctx context.Context, uint64, loc Location) error // or upd
	GetUserLocation(ctx context.Context, userId uint64) (bool, Location, error)
	SearchUsersNearby(ctx context.Context, loc Location, radius uint64) ([]uint64, error)

	RemoveAllForUser(ctx context.Context, userId uint64) error
}
