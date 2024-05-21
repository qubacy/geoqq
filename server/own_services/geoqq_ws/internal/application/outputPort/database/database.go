package database

import "context"

type MateDatabase interface {
	InsertMateMessage(ctx context.Context,
		text string, lon, lat float64) error
}

type GeoDatabase interface {
	InsertGeoMessage(ctx context.Context,
		text string, lon, lat float64) error
}

type Database interface {
	UserDatabase
	MateDatabase
	GeoDatabase
}
