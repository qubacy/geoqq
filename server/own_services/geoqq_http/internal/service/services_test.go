package service

import (
	"context"
	"fmt"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_Context(t *testing.T) {
	type Key string

	ctx := context.Background()
	ctx = context.WithValue(ctx, Key("name"), "value")

	fmt.Println(ctx.Value(Key("name")))
	unknownValue := ctx.Value(Key("name"))

	if unknownValue != nil {
		fmt.Println("by unknownName get:", unknownValue)
	} else {
		fmt.Println("by unknownName get:", nil)
	}
}
