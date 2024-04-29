package impl

import (
	"context"
	"fmt"
	utl "geoqq/pkg/utility"
)

// Keys
// -----------------------------------------------------------------------

func keyAddImageBlockedForUser(id uint64) string {
	return fmt.Sprintf("add_image_blocked_for_%v", id)
}

// Actions
// -----------------------------------------------------------------------

func (s *ImageService) updateAddImageCache(ctx context.Context, userId uint64) error {
	err := s.cache.SetWithTTL(ctx,
		keyAddImageBlockedForUser(userId), "1",
		s.addImageParams.BlockingTime,
	)
	if err != nil {
		return utl.NewFuncError(s.updateAddImageCache, err)
	}
	return nil
}
