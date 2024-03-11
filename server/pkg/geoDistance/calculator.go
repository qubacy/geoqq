package geoDistance

type Point struct {
	Latitude  float64
	Longitude float64
} // Values in degrees!

type Calculator interface {
	CalculateDistance(p1, p2 Point) float64
}
