package domain

type UserStorageBackground interface {
	UpdateBgrLastActionTimeForUser(id uint64)
	DeleteBgrMateChatsForUser(id uint64)
}

// -----------------------------------------------------------------------

type Background interface {
	UserStorageBackground
}
