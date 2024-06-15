package database

import "context"

type GeoDatabase interface {
	InsertGeoMessage(ctx context.Context, fromUserId uint64,
		text string, lon, lat float64) (uint64, error)
}
