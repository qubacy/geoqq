package impl

import "errors"

var (
	ErrLoggerTypeIsNotDefined error = errors.New("logger type is not defined")
	ErrNotImplemented         error = errors.New("not implemented")
)
