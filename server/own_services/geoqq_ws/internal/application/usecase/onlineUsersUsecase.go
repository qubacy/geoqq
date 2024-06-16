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

func (u *OnlineUsersUsecase) SetUserToOnline(userId uint64) {
	u.onlineUsers.Store(userId, true)
}

func (u *OnlineUsersUsecase) RemoveUserFromOnline(userId uint64) {
	u.onlineUsers.Delete(userId)
}

func (u *OnlineUsersUsecase) GetOnlineUserIds() []uint64 {
	userIds := []uint64{}
	u.onlineUsers.Range(func(key, value any) bool {
		userIds = append(userIds, key.(uint64))
		return true
	})

	return userIds
}
