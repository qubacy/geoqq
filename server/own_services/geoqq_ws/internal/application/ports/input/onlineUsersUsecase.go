package input

type OnlineUsersUsecase interface {
	SetUserToOnline(userId uint64)
	RemoveUserFromOnline(userId uint64)
	GetOnlineUserIds() []uint64
}
