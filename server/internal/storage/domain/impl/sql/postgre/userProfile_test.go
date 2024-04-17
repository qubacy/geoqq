package postgre

import (
	"fmt"
	"testing"

	"github.com/google/uuid"
)

func Test_Uuid(t *testing.T) {
	for i := 0; i < 3; i++ {
		uuidValue := uuid.NewString()
		fmt.Printf("%v ---> %v\n", uuidValue, len(uuidValue))
	}
}
