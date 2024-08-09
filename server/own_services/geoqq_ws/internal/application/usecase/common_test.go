package usecase

import (
	domain "common/pkg/domain/geoqq"
	"fmt"
	"reflect"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_reflect_TypeOf(t *testing.T) {
	var mm *domain.MateMessageWithChat = nil
	fmt.Printf("type of: %v\n", reflect.TypeOf(mm))
	fmt.Printf("type of: %v\n", reflect.TypeOf(1))
	fmt.Printf("type of: %v\n", reflect.TypeOf(1.1))

	//...
}
