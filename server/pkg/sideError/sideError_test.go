package sideError

import (
	"fmt"
	"testing"
)

func Test_SideError(t *testing.T) {
	err := NewSideError("Test error", Client)
	fmt.Println(err)

	fmt.Println(err.Text)
	fmt.Println(err.Side)

	// ***

	var baseErr error = err
	fmt.Println(baseErr)
}
