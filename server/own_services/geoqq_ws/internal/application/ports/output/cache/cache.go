package cache

import "context"

const (
	TextCacheDisabled = "cache disabled"
)

type Location struct {
	Lat float64
	Lon float64
}

type Cache interface {
	AddUserRadius(ctx context.Context, userId uint64, radius uint64) error
	GetUserRadius(ctx context.Context, userId uint64) (uint64, error)

	AddUserLocation(ctx context.Context, userId uint64, loc Location) error // or upd
	GetUserLocation(ctx context.Context, userId uint64) (bool, Location, error)
	SearchUsersNearby(ctx context.Context, loc Location, radius uint64) ([]uint64, error)

	RemoveAllForUser(ctx context.Context, userId uint64) error
}
