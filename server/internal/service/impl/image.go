package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	"geoqq/pkg/file"
)

type ImageService struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
}

func newImageService(deps Dependencies) *ImageService {
	instance := &ImageService{
		fileStorage:   deps.FileStorage,
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// -----------------------------------------------------------------------

func (s *ImageService) GetImageById(ctx context.Context, imageId uint64) (file.Image, error) {
	s.fileStorage.LoadImage(ctx)
}

func (s *ImageService) GetImagesByIds(ctx context.Context, imageIds []uint64) ([]file.Image, error) {

}
