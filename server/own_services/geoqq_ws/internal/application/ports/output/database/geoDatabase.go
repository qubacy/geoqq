package database

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type GeoDatabase interface {
	InsertGeoMessage(ctx context.Context, fromUserId uint64, text string, lon, lat float64) (uint64, error)
	GetGeoMessageWithId(ctx context.Context, id uint64) (*domain.GeoMessage, error)
}
