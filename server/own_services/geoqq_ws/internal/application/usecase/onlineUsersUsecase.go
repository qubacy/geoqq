package usecase

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/output/cache"
	"sync"
	"time"
)

type OnlineUsersParams struct {
	TempDatabase        cache.Cache
	CacheRequestTimeout time.Duration
}

// -----------------------------------------------------------------------

type OnlineUsersUsecase struct {
	onlineUsers         sync.Map
	tempDb              cache.Cache
	cacheRequestTimeout time.Duration
}

func NewOnlineUsersUsecase(params *OnlineUsersParams) *OnlineUsersUsecase {
	return &OnlineUsersUsecase{
		onlineUsers:         sync.Map{},
		tempDb:              params.TempDatabase,
		cacheRequestTimeout: params.CacheRequestTimeout,
	}
}

// public
// -----------------------------------------------------------------------

func (u *OnlineUsersUsecase) SetUsersToOnline(userIds ...uint64) {
	for _, userId := range userIds {
		u.onlineUsers.Store(userId, true)
	}
}

func (u *OnlineUsersUsecase) RemoveUsersFromOnline(userIds ...uint64) {
	for _, userId := range userIds {
		u.onlineUsers.Delete(userId)

		if u.tempDb != nil {
			ctx := context.Background()
			ctx, cancel := context.WithTimeout(ctx, u.cacheRequestTimeout)
			defer cancel()

			if err := u.tempDb.RemoveAllForUser(ctx, userId); err != nil {
				logger.Error("%v", utl.NewFuncError(u.RemoveUsersFromOnline, err))
			}

		} else {
			logger.Warning(cache.TextCacheDisabled)
		}
	}
}

// -----------------------------------------------------------------------

func (u *OnlineUsersUsecase) GetOnlineUserIds() []uint64 {
	userIds := []uint64{}
	u.onlineUsers.Range(func(key, value any) bool {
		userIds = append(userIds, key.(uint64))
		return true
	})

	return userIds
}

func (u *OnlineUsersUsecase) UserIsOnline(userId uint64) bool {
	_, loaded := u.onlineUsers.Load(userId)
	return loaded
}
