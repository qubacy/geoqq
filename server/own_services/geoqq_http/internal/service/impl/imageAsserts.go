package impl

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
)

// From Cache
// -----------------------------------------------------------------------

func (s *HasherAndStorages) assertAddImageNotBlockedForUser(
	ctx context.Context, userId uint64) error {
	if !s.enableCache {
		logger.Warning("cache disabled")
		return nil
	}

	addImageBlocked, err := s.cache.Exists(ctx,
		imageCacheKey.AddImageBlockedForUser(userId))
	if err != nil {
		return ec.New(utl.NewFuncError(s.assertAddImageNotBlockedForUser, err),
			ec.Server, ec.CacheError)
	}
	if addImageBlocked {
		return ec.New(ErrAddImageBlockedForUserWithId(userId),
			ec.Client, ec.AddImageBlockedForUser)
	}
	return nil
}
