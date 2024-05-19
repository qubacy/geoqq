package domain

type UserStorageBackground interface {
	UpdateBgrLastActionTimeForUser(id uint64)
	UpdateBgrLocationForUser(id uint64, longitude, latitude float64)

	DeleteBgrMateChatsForUser(id uint64)
}

// -----------------------------------------------------------------------

type Background interface {
	UserStorageBackground
}
