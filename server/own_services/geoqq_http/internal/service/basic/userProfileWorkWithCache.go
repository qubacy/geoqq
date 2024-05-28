package impl

import (
	utl "common/pkg/utility"
	"context"
	"fmt"
)

// Keys
// -----------------------------------------------------------------------

type userProfileCacheKeyFormatter struct{}

func (u *userProfileCacheKeyFormatter) ChangeUsernameBlockedForUser(id uint64) string {
	return fmt.Sprintf("change_username_blocked_for_user_%v", id)
}

var userProfileCacheKey = userProfileCacheKeyFormatter{}

// Actions
// -----------------------------------------------------------------------

func (u *UserProfileService) updateDeletedUserCache(
	ctx context.Context, userId uint64) error {

	sourceFunc := u.updateDeletedUserCache
	cacheKey := authCacheKey.DeletedUser(userId)

	// to make the access token invalid!

	err := u.cache.SetWithTTL(ctx, cacheKey, "1", u.accessTokenTTL)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}

func (u *UserProfileService) updateChangeUsernameCache(
	ctx context.Context, userId uint64) error {

	sourceFunc := u.updateChangeUsernameCache
	cacheKey := userProfileCacheKey.ChangeUsernameBlockedForUser(userId)
	blockingTime := u.userParams.UpdateUsernameParams.BlockingTime

	err := u.cache.SetWithTTL(ctx, cacheKey, "1", blockingTime)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}
