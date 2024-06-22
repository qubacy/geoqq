package input

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type UserIdWithGeoMessage struct {
	UserId  uint64
	MateMsg dd.GeoMessage
}

type GeoMessageUsecase interface {
	AddGeoMessage(ctx context.Context, userId uint64, text string, lon, lat float64) error
	GetFbChansForGeoMessages() []<-chan UserIdWithGeoMessage
}
