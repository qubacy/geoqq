package rabbit

import (
	"fmt"
	"testing"
	"time"
)

func Test_timeToString(t *testing.T) {
	fmt.Println(time.Now().UTC().UnixMilli())

	dur := 5 * time.Minute
	fmt.Printf("%v", dur.Milliseconds())
}
