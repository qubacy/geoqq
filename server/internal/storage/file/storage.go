package file

import (
	"context"
	"geoqq/pkg/file"
)

type ImageStorage interface {
	LoadImage(ctx context.Context, id uint64, ext file.ImageExt) (file.Image, error)
	SaveImage(ctx context.Context, image file.Image) error
}

type Storage interface {
	ImageStorage
}
