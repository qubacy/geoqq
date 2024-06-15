package input

import (
	"context"
)

type GeoUsecase interface {
	AddGeoMessage(ctx context.Context,
		text string, lon, lat float64) error
}
