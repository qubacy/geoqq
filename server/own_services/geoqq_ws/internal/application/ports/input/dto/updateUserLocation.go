package dto

type UpdateUserLocation struct {
	UserId    uint64
	Longitude float64
	Latitude  float64
	Radius    uint64
}
