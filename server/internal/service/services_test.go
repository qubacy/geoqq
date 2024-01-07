package service

import (
	"context"
	"fmt"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_Context(t *testing.T) {
	ctx := context.Background()
	ctx = context.WithValue(ctx, "name", "value")

	fmt.Println(ctx.Value("name"))
	unknownValue := ctx.Value("unknownName")

	if unknownValue != nil {
		fmt.Println("by unknownName get:", unknownValue)
	} else {
		fmt.Println("by unknownName get:", nil)
	}
}
