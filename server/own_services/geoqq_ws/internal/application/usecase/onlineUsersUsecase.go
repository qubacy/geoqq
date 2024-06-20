package usecase

import (
	"sync"
)

type OnlineUsersUsecase struct {
	onlineUsers sync.Map
}

func NewOnlineUsersUsecase() *OnlineUsersUsecase {
	return &OnlineUsersUsecase{
		onlineUsers: sync.Map{},
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
