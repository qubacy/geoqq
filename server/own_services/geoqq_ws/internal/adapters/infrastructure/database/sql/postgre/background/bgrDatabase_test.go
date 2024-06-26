package background

import (
	"log"
	"testing"
)

func Test_rangeLoop(t *testing.T) {
	var ii []int = nil
	for _, one := range ii { // warn!
		log.Println(one)
	}
}
