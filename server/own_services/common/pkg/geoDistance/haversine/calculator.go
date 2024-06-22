package haversine

import (
	"common/pkg/geoDistance"

	"github.com/asmarques/geodist"
)

type Calculator struct{}

func NewCalculator() *Calculator {
	return &Calculator{}
}

// public
// -----------------------------------------------------------------------

func (c *Calculator) CalculateDistance(p1, p2 geoDistance.Point) float64 {
	pp1 := geodist.Point{Lat: p1.Latitude, Long: p1.Longitude}
	pp2 := geodist.Point{Lat: p2.Latitude, Long: p2.Longitude}
	return geodist.HaversineDistance(pp1, pp2) * 1000 // in meters!
}
