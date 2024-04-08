package logger

import "errors"

var (
	ErrLoggerIsAlreadyInitialized error = errors.New("logger is already initialized")
)
