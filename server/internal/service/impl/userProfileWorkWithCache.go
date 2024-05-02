package impl

import (
	"context"
	utl "geoqq/pkg/utility"
)

func (u *UserProfileService) updateDeletedUserCache(
	ctx context.Context, userId uint64) error {

	sourceFunc := u.updateDeletedUserCache
	cacheKey := authCacheKey.DeletedUser(userId)

	// to make the access token invalid!

	err := u.cache.SetWithTTL(ctx,
		cacheKey, "1", u.accessTokenTTL,
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}
