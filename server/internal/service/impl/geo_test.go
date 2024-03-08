package impl

import (
	"fmt"
	"testing"

	"github.com/asmarques/geodist"
)

// experiments
// -----------------------------------------------------------------------

func Test_geodist_HaversineDistance(t *testing.T) {
	lhs := geodist.Point{Lat: 55.75222, Long: 37.61556}
	rhs := geodist.Point{Lat: 56.01839, Long: 92.86717}
	fmt.Println(geodist.HaversineDistance(lhs, rhs))
}
