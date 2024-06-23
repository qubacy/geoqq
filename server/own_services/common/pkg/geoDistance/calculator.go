package geoDistance

type Point struct {
	Latitude  float64
	Longitude float64
} // Values in degrees!

func MakePoint(lat, lon float64) Point {
	return Point{
		Latitude:  lat,
		Longitude: lon,
	}
}

type Calculator interface {
	CalculateDistance(p1, p2 Point) float64
}
