package impl

import (
	"context"
	"encoding/base64"
	"errors"
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
		return nil, ec.New(utl.NewFuncError(s.GetImageById, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return nil, ec.New(ErrImageNotFound,
			ec.Client, ec.ImageNotFound) // client?
	}

	// ***

	image, err := s.fileStorage.LoadImage(ctx, imageId)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetImageById, err),
			ec.Server, ec.FileStorageError)
	}

	return image, nil
}

func (s *ImageService) GetImagesByIds(ctx context.Context, imageIds []uint64) (*file.Images, error) {
	imageIds = utl.RemoveDuplicatesFromSlice(imageIds)

	exists, err := s.domainStorage.HasAvatars(ctx, imageIds)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetImagesByIds, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return nil, ec.New(ErrOneOrMoreImagesNotFound,
			ec.Client, ec.OneOrMoreImagesNotFound)
	}

	// ***

	images := make([]*file.Image, 0, len(imageIds)) // reserver mem
	for _, imageId := range imageIds {
		image, err := s.GetImageById(ctx, imageId) // TODO: don't have to do a second check
		if err != nil {
			return nil, utl.NewFuncError(s.GetImagesByIds, err) // with error for client!
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

	return imageId, nil // Use for update user profile...
}

// private
// -----------------------------------------------------------------------

// base implementation!
func (p *HasherAndStorages) addImageToUser(ctx context.Context,
	ext int, content string, userId uint64) (uint64, error) {

	imageExt := file.ImageExt(ext)
	if !imageExt.IsValid() {
		return 0, ec.New(ErrUnknownImageExtension,
			ec.Client, ec.UnknownImageExtension)
	}
	if len(content) == 0 { // avatar body check also on delivery layout!
		return 0, ec.New(ErrImageContentEmpty,
			ec.Client, ec.ImageContentIsEmpty)
	}

	// TODO: check mime type!
	// TODO: remove avatar meta data!

	image := file.NewImageWithoutId(imageExt, content)
	avatarContentBytes, err := base64.StdEncoding.DecodeString(content)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Client, ec.ImageBodyIsNotBase64) // or server?
	}

	avatarHash, err := p.hashManager.NewFromBytes(avatarContentBytes)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Server, ec.HashManagerError)
	}

	// ***

	avatarId, err := p.domainStorage.InsertAvatar(ctx, userId, avatarHash)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Server, ec.DomainStorageError)
	}
	image.Id = avatarId

	err = p.fileStorage.SaveImage(ctx, image)
	if err != nil {
		err = errors.Join(
			err,
			p.domainStorage.DeleteAvatarWithId(ctx, avatarId),
		)
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Server, ec.FileStorageError)
	}

	return avatarId, nil // OK!
}
