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

func Test_timeToUtcToUtc(t *testing.T) {
	fmt.Println(time.Now())
	fmt.Println(time.Now().UTC())
	fmt.Println(time.Now().UTC().UTC())

	fmt.Println(time.Now().UnixMilli())
	fmt.Println(time.Now().UTC().UnixMilli())
	fmt.Println(time.Now().UTC().UTC().UnixMilli())
	//...
}
