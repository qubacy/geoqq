package postgre

import (
	"errors"
	"fmt"
)

var (
	ErrNoRows           = errors.New("no rows")
	ErrUnexpectedResult = errors.New("unexpected result")
	ErrNotImplemented   = errors.New("not implemented")

	ErrUpdateFailed = errors.New("update failed")
	ErrInsertFailed = errors.New("insert failed")
	ErrDeleteFailed = errors.New("delete failed")

	ErrInvalidParams = errors.New("invalid params")
)

func ErrNilInputParameterWithName(name string) error {
	return fmt.Errorf("nil input parameter `%v", name)
}
