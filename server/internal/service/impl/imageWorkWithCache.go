package impl

import (
	"context"
	"encoding/json"
	"fmt"
	"geoqq/pkg/file"
	utl "geoqq/pkg/utility"
)

// In a separate class?

// Keys
// -----------------------------------------------------------------------

type imageCacheKeyFormatter struct{}

func (*imageCacheKeyFormatter) AddImageBlockedForUser(id uint64) string {
	return fmt.Sprintf("add_image_blocked_for_user_%v", id)
}

func (*imageCacheKeyFormatter) SavedImage(id uint64) string {
	return fmt.Sprintf("saved_image_%v", id)
}

var imageCacheKey = imageCacheKeyFormatter{}

// Actions
// -----------------------------------------------------------------------

func (s *ImageService) updateAddImageCache(ctx context.Context, userId uint64) error {
	err := s.cache.SetWithTTL(ctx,
		imageCacheKey.AddImageBlockedForUser(userId), "1",
		s.ImageParams.AddImageParams.BlockingTime,
	)
	if err != nil {
		return utl.NewFuncError(s.updateAddImageCache, err)
	}
	return nil
}

func (s *ImageService) loadImageFromCache(ctx context.Context, id uint64) (*file.Image, error) {
	sourceFunc := s.loadImageFromCache
	keySavedImage := imageCacheKey.SavedImage(id)

	imageInCache, err := s.cache.Exists(ctx, keySavedImage)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	if !imageInCache {
		return nil, ErrImageWithIdNotInCache(id)
	}

	jsonStr, err := s.cache.Get(ctx, keySavedImage)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	image := file.Image{}
	if err = json.Unmarshal([]byte(jsonStr), &image); err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return &image, nil
}

func (s *ImageService) saveImageToCache(ctx context.Context, image *file.Image) error {
	if image == nil {
		return ErrNilInputParameterWithName("Image")
	}

	jsonBytes, err := json.Marshal(image) // as response?
	if err != nil {
		return utl.NewFuncError(s.saveImageToCache, err)
	}

	err = s.cache.SetWithTTL(ctx,
		imageCacheKey.SavedImage(image.Id), string(jsonBytes),
		s.ImageParams.CacheTtl,
	)
	if err != nil {
		return utl.NewFuncError(s.saveImageToCache, err)
	}
	return nil // OK
}
