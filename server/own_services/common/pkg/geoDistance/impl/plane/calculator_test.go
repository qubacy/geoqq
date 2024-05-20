package plane

import (
	"common/pkg/geoDistance"
	"fmt"
	"testing"
)

func Test_CalculateDistance(t *testing.T) {
	c := NewCalculator()
	p1 := geoDistance.Point{Latitude: 55.75222, Longitude: 37.61556}
	p2 := geoDistance.Point{Latitude: 56.01839, Longitude: 92.86717}
	fmt.Println(c.CalculateDistance(p1, p2))
}
