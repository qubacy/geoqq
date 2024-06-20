package input

type OnlineUsersUsecase interface {
	SetUsersToOnline(userIds ...uint64)
	RemoveUsersFromOnline(userIds ...uint64)

	GetOnlineUserIds() []uint64
	UserIsOnline(userId uint64) bool
}
