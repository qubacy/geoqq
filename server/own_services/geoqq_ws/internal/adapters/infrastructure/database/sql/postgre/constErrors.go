package postgre

import "errors"

var (
	ErrNotImplemented = errors.New("not implemented")
	ErrUpdateFailed   = errors.New("update failed")
)
