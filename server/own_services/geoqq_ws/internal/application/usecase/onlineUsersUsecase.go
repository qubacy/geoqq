package usecase

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"geoqq_ws/internal/application/ports/output/cache"
	"sync"
)

type OnlineUsersParams struct {
	TempDatabase cache.Cache
}

type OnlineUsersUsecase struct {
	onlineUsers sync.Map
	tempDb      cache.Cache
}

func NewOnlineUsersUsecase(params *OnlineUsersParams) *OnlineUsersUsecase {
	return &OnlineUsersUsecase{
		onlineUsers: sync.Map{},
		tempDb:      params.TempDatabase,
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
			if err := u.tempDb.RemoveAllForUser(userId); err != nil {
				logger.Error("%v", utl.NewFuncError(u.RemoveUsersFromOnline, err))
			}
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
