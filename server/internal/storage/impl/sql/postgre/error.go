package postgre

import "errors"

var (
	ErrNoRows           error = errors.New("No rows")
	ErrUnexpectedResult error = errors.New("Unexpected result")
	ErrUpdateFailed     error = errors.New("Update failed")
)
