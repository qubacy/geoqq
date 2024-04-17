package domain

type UserStorageBackground interface {
	UpdateBgrLastActivityTimeForUser(id uint64)
	DeleteBgrMateChatsForUser(id uint64)
}

// -----------------------------------------------------------------------

type Background interface {
	UserStorageBackground
}
