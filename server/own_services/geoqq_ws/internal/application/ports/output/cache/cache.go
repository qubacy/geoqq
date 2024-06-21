package cache

type Location struct {
	Lat float64
	Lon float64
}

type Cache interface {
	AddUserLocation(userId uint64, loc Location) error // or upd
	GetUserLocation(userId uint64) (bool, Location, error)
	SearchUsersNearby(loc Location, radius uint64) ([]uint64, error)

	RemoveAllForUser(userId uint64) error
}
