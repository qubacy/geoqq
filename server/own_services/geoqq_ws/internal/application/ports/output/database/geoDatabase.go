package database

import "context"

type GeoDatabase interface {
	InsertGeoMessage(ctx context.Context,
		text string, lon, lat float64) error
}
