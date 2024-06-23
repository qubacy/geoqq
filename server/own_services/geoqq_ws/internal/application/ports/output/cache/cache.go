package cache

import (
	geo "common/pkg/geoDistance"
	"context"
)

const (
	TextCacheDisabled = "cache disabled"
)

type Location struct {
	Lat float64
	Lon float64
}

func (l Location) ToGeoPoint() geo.Point {
	return geo.Point{
		Longitude: l.Lon,
		Latitude:  l.Lat,
	}
}

type GeoSpace struct {
	Location Location
	Radius   uint64
}

type UserIdWithLocation = map[uint64]Location
type UserIdWithRadius = map[uint64]uint64
type UserIdWithGeoSpace = map[uint64]GeoSpace

func ToKeys(mp UserIdWithLocation) []uint64 {
	keys := []uint64{}
	for k := range mp {
		keys = append(keys, k)
	}
	return keys
}

// -----------------------------------------------------------------------

type Cache interface {
	AddUserRadius(ctx context.Context, userId uint64, radius uint64) error
	GetUserRadius(ctx context.Context, userId uint64) (uint64, error)
	GetUserRadiuses(ctx context.Context, userIds ...uint64) (UserIdWithRadius, error)

	AddUserLocation(ctx context.Context, userId uint64, loc Location) error // or upd?
	GetUserLocation(ctx context.Context, userId uint64) (bool, Location, error)

	SearchUsersNearby(ctx context.Context, loc Location, radius uint64) ([]uint64, error)
	SearchUsersWithLocationsNearby(ctx context.Context, loc Location, radius uint64) (UserIdWithLocation, error)

	// ***

	AddUserGeoSpace(ctx context.Context, userId uint64, gs GeoSpace) error
	GetUserGeoSpaces(ctx context.Context, userIds ...uint64) (UserIdWithGeoSpace, error)

	RemoveAllForUser(ctx context.Context, userId uint64) error
}
