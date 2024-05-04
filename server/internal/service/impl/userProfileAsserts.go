package impl

import (
	"context"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"
)

func (s *UserProfileService) assertChangeUsernameNotBlockedForUser(
	ctx context.Context, userId uint64) error {
	sourceFunc := s.assertChangeUsernameNotBlockedForUser
	if !s.enableCache {
		logger.Warning("cache disabled")
		return nil
	}

	changeUsernameBlocked, err := s.cache.Exists(ctx,
		userProfileCacheKey.ChangeUsernameBlockedForUser(userId),
	)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.CacheError)
	}

	if changeUsernameBlocked {
		return ec.New(ErrChangeUsernameBlockedForUserWithId(userId),
			ec.Client, ec.ChangeUsernameBlockedForUser)
	}

	return nil
}
