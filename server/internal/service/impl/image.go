package impl

import (
	"context"
	"encoding/base64"
	"geoqq/internal/service/dto"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	utl "geoqq/pkg/utility"
)

type ImageService struct {
	HasherAndStorages
}

func newImageService(deps Dependencies) *ImageService {
	instance := &ImageService{
		HasherAndStorages{
			fileStorage:   deps.FileStorage,
			domainStorage: deps.DomainStorage,
			hashManager:   deps.HashManager,
		},
	}

	return instance
}

// public
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

func (s *ImageService) GetImagesByIds(ctx context.Context, imageIds []uint64) (*file.Images, error) {
	imageIds = utl.RemoveDuplicatesFromSlice(imageIds)

	exists, err := s.domainStorage.HasAvatars(ctx, imageIds)
	if err != nil {
		return nil, utl.NewFuncError(s.GetImagesByIds,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return nil, utl.NewFuncError(s.GetImagesByIds,
			ec.New(ErrOneOrMoreImagesNotFound, ec.Client, ec.OneOrMoreImagesNotFound))
	}

	// ***

	images := []*file.Image{}
	for _, imageId := range imageIds {
		image, err := s.GetImageById(ctx, imageId)
		if err != nil {
			return nil, utl.NewFuncError(s.GetImagesByIds, err) // already error for client!
		}

		images = append(images, image)
	}

	return file.NewImages(images), nil
}

func (s *ImageService) AddImageToUser(ctx context.Context, userId uint64,
	input dto.ImageForAddToUserInp) (uint64, error) {

	imageId, err := s.addImageToUser(ctx,
		input.Ext, input.Content, userId)

	if err != nil {
		return 0, utl.NewFuncError(s.AddImageToUser, err)
	}

	return imageId, nil
}

// private
// -----------------------------------------------------------------------

func (p *HasherAndStorages) avatarIdWithError(err error, side, code int) (uint64, error) {
	return 0, utl.NewFuncError(
		p.addImageToUser, ec.New(
			err, side, code),
	)
}

// base implementation!
func (p *HasherAndStorages) addImageToUser(ctx context.Context,
	ext int, content string, userId uint64) (uint64, error) {

	imageExt := file.ImageExt(ext)
	if !imageExt.IsValid() {
		return p.avatarIdWithError(ErrUnknownImageExtension, ec.Client, ec.UnknownAvatarExtension)
	}
	if len(content) == 0 { // avatar body check also on delivery layout!
		return p.avatarIdWithError(ErrImageBodyEmpty, ec.Client, ec.AvatarBodyEmpty)
	}

	// ***

	image := file.NewImageWithoutId(imageExt, content)
	avatarContentBytes, err := base64.StdEncoding.DecodeString(content)
	if err != nil {
		return p.avatarIdWithError(err, ec.Client, ec.AvatarBodyIsNotBase64) // or server?
	}

	avatarHash, err := p.hashManager.NewFromBytes(avatarContentBytes)
	if err != nil {
		return p.avatarIdWithError(err, ec.Server, ec.HashManagerError)
	}

	// ***

	avatarId, err := p.domainStorage.InsertAvatar(ctx, userId, avatarHash)
	if err != nil {
		return p.avatarIdWithError(err, ec.Server, ec.DomainStorageError)
	}
	image.Id = avatarId

	err = p.fileStorage.SaveImage(ctx, image)
	if err != nil {
		_ = p.domainStorage.DeleteAvatarWithId(ctx, avatarId)

		return p.avatarIdWithError(err, ec.Server, ec.FileStorageError)
	}
	return avatarId, nil
}
