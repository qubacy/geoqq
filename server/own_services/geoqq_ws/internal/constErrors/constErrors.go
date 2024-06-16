package constErrors

import (
	"errors"
	"fmt"
)

var (
	ErrNotImplemented         = errors.New("not implemented")
	ErrInputParamNotSpecified = errors.New("input parameter not specified")
)

func ErrInputParamWithTypeNotSpecified(name string) error {
	return fmt.Errorf("input param `%v` not specified", name)
}
