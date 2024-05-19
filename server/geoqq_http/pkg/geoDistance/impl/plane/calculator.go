package plane

import (
	"geoqq_http/pkg/geoDistance"
	"math"
)

type Calculator struct{}

func NewCalculator() *Calculator {
	return &Calculator{}
}

// public
// -----------------------------------------------------------------------

// Not working!
func (c *Calculator) CalculateDistance(p1, p2 geoDistance.Point) float64 {
	part1 := math.Pow((p1.Latitude - p2.Latitude), 2)
	part2 := math.Pow((p1.Longitude - p2.Longitude), 2)
	return math.Sqrt(part1 + part2)
}
