package background

type UserDatabase interface {
	UpdateBgrLastActionTimeForUser(id uint64)
	UpdateBgrLocationForUser(id uint64, longitude, latitude float64)
}
