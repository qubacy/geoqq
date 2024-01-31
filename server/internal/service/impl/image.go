package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	utl "geoqq/pkg/utility"
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

func (s *ImageService) GetImageById(ctx context.Context, imageId uint64) (*file.Image, error) {
	exists, err := s.domainStorage.HasAvatar(ctx, imageId)
	if err != nil {
		return nil, utl.NewFuncError(s.GetImageById,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return nil, utl.NewFuncError(s.GetImageById,
			ec.New(ErrImageNotFound, ec.Client, ec.ImageNotFound)) // client?
	}

	// ***

	image, err := s.fileStorage.LoadImage(ctx, imageId)
	if err != nil {
		return nil, utl.NewFuncError(s.GetImageById,
			ec.New(err, ec.Server, ec.FileStorageError))
	}

	return image, nil
}

func (s *ImageService) GetImagesByIds(ctx context.Context, imageIds []uint64) ([]*file.Image, error) {
	images := []*file.Image{}
	for _, imageId := range imageIds {
		image, err := s.GetImageById(ctx, imageId)
		if err != nil {
			return nil, utl.NewFuncError(s.GetImagesByIds, err)
		}

		images = append(images, image)
	}

	return images, nil
}
