package resource

import (
	"context"
	"geoqq/pkg/resource"
)

type ImageStorage interface {
	LoadImage(ctx context.Context, id uint64) (resource.Image, error)
	SaveImage(ctx context.Context, id uint64) error
}

type Storage interface {
}
