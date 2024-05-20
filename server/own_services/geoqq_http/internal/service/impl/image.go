package impl

import (
	"common/pkg/file"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"encoding/base64"
	"errors"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	"geoqq_http/internal/service/dto"
	"net/http"

	exifremove "github.com/scottleedavis/go-exif-remove"
)

type ImageService struct {
	HasherAndStorages
	ImageParams ImageParams
}

func newImageService(deps Dependencies) *ImageService {
	instance := &ImageService{
		HasherAndStorages: HasherAndStorages{
			enableCache: deps.EnableCache,
			cache:       deps.Cache,

			fileStorage:   deps.FileStorage,
			domainStorage: deps.DomainStorage,
			hashManager:   deps.HashManager,
		},
		ImageParams: deps.ImageParams,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *ImageService) GetImageById(ctx context.Context, _, imageId uint64) (
	*file.Image, error,
) {
	/*
		Action List:
			1. Attempt to read from cache (if ok return immediately).
			2. Check if the image is in the domain storage.
			3. Load image from file storage (if domain ok, then file ok?).
			4. Save the image to cache.
	*/

	sourceFunc := s.GetImageById

	var err error = nil
	var image *file.Image = nil

	if s.enableCache {
		image, err = s.loadImageFromCache(ctx, imageId)
		if err != nil { // not a critical error (cache miss?)
			logger.Warning("%v", err)
		} else {
			logger.Debug("image %v loaded from cache", imageId)
			return image, nil
		}
	} else {
		logger.Warning("cache disabled")
	}

	// ***

	err = assertImageWithIdExists(ctx,
		s.domainStorage, imageId,
		ec.ImageNotFound,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	image, err = s.fileStorage.LoadImage(ctx, imageId)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.FileStorageError)
	}

	if s.enableCache {
		if err = s.saveImageToCache(ctx, image); err != nil {
			logger.Warning("%v", err)
		} else {
			logger.Debug("image %v saved in cache", imageId)
		}
	}
	return image, nil
}

func (s *ImageService) GetImagesByIds(ctx context.Context, userId uint64, imageIds []uint64) (*file.Images, error) {
	sourceFunc := s.GetImagesByIds
	imageIds = utl.RemoveDuplicatesFromSlice(imageIds)

	exists, err := s.domainStorage.HasAvatars(ctx, imageIds)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return nil, ec.New(ErrOneOrMoreImagesNotFound,
			ec.Client, ec.OneOrMoreImagesNotFound)
	}

	// ***

	images := make([]*file.Image, 0, len(imageIds)) // reserver mem
	for _, imageId := range imageIds {
		image, err := s.GetImageById(ctx, userId, imageId) // TODO: don't have to do a second check
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err) // with error for client!
		}

		images = append(images, image)
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return file.NewImages(images), nil
}

func (s *ImageService) AddImageToUser(ctx context.Context, userId uint64,
	input *dto.ImageForAddToUserInp) (uint64, error) {
	if input == nil {
		return 0, ec.New(ErrNilInputParameterWithName("ImageForAddToUserInp"),
			ec.Server, ec.ServerError)
	}
	sourceFunc := s.AddImageToUser

	if err := s.assertAddImageNotBlockedForUser(ctx, userId); err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	imageId, err := s.addImageToUser(ctx, input.Ext, input.Content, userId)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	if s.enableCache {
		if err := s.updateAddImageCache(ctx, userId); err != nil {
			logger.Warning("%v", err)
		} else {
			logger.Debug("add image cache updated for user %v", userId)
		}
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return imageId, nil // Use for update user profile...
}

// private
// -----------------------------------------------------------------------

// base implementation!
func (p *HasherAndStorages) addImageToUser(ctx context.Context,
	ext int, content string, userId uint64) (uint64, error) {

	// prepare...

	imageExt := file.ImageExt(ext)
	if !imageExt.IsValid() {
		return 0, ec.New(ErrUnknownImageExtension,
			ec.Client, ec.UnknownImageExtension)
	}
	if len(content) == 0 { // avatar body check also on delivery layout!
		return 0, ec.New(ErrImageContentEmpty,
			ec.Client, ec.ImageContentIsEmpty)
	}

	avatarContentBytes, err := base64.StdEncoding.DecodeString(content)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Client, ec.ImageBodyIsNotBase64) // or server?
	}
	mimeType := http.DetectContentType(avatarContentBytes)
	if !file.ValidImageMimeType(mimeType) {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, nil),
			ec.Client, ec.UnsupportedImageMimeType)
	}

	noExifBytes, err := exifremove.Remove(avatarContentBytes)
	if err != nil {
		logger.Warning("failed to remove exif due to %v", err)
	} else {
		avatarContentBytes = noExifBytes // rewrite!
	}

	// to domain storage

	avatarHash, err := p.hashManager.NewFromBytes(avatarContentBytes)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Server, ec.HashManagerError)
	}
	avatarId, err := p.domainStorage.InsertAvatar(ctx, userId, avatarHash)
	if err != nil {
		return 0, ec.New(utl.NewFuncError(p.addImageToUser, err),
			ec.Server, ec.DomainStorageError)
	}

	// to file storage

	image := file.NewImage(avatarId, imageExt, content)
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
