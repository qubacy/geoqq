package internal

type UserLocation struct {
	Longitude float64
	Latitude  float64
}

type Client struct {
	UserId uint64

	KnownLocation bool
	Location      UserLocation
}

func MakeEmptyClient() Client {
	return Client{}
}
