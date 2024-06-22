package database

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type GeoDatabase interface {
	InsertGeoMessage(ctx context.Context, fromUserId uint64, text string, lon, lat float64) (uint64, error)
	GetGeoMessageWithId(ctx context.Context, id uint64) (*dd.GeoMessage, error)
}
