package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type UserIdWithGeoMessage struct {
	UserId     uint64
	GeoMessage *domain.GeoMessage
}

type GeoMessageUsecase interface {
	ForwardGeoMessage(ctx context.Context, gm *domain.GeoMessage, lon, lat float64) error
	AddGeoMessage(ctx context.Context, userId uint64, text string, lon, lat float64) error
	GetFbChansForGeoMessages() []<-chan UserIdWithGeoMessage
}
