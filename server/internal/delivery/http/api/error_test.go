package api

import (
	"fmt"
	"testing"
)

func Test_ErrSomeParametersAreMissingWithNames(t *testing.T) {
	err := ErrSomeParametersAreMissingWithNames([]string{"1", "2", "3"})
	fmt.Println(err.Error())
}
