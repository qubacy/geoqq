package background

import (
	"log"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_rangeLoop(t *testing.T) {
	var ii []int = nil
	for _, one := range ii { // warn!
		log.Println(one)
	}

	// ***

	ii = make([]int, 0, 10)
	for _, one := range ii { // warn!
		log.Println(one)
	}
}
