package sideError

import (
	"errors"
	"fmt"
	"testing"
)

func Test_New(t *testing.T) {
	err := New("Text error", Server)
	sideErr := errors.Unwrap(err)
	fmt.Println(sideErr) // !
}
