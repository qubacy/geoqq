package dto

import (
	"errors"
	"fmt"
)

var (
	ErrNilInputParameter = errors.New("nil input parameter")
)

func ErrNilInputParameterWithName(name string) error {
	return fmt.Errorf("nil input parameter `%v", name)
}
