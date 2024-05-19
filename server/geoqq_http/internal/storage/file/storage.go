package file

import (
	"context"
	"geoqq_http/pkg/file"
)

type ImageStorage interface {
	HasImage(ctx context.Context, id uint64) (bool, error)
	LoadImage(ctx context.Context, id uint64) (*file.Image, error)
	SaveImage(ctx context.Context, image *file.Image) error
}

type Storage interface {
	ImageStorage
}
