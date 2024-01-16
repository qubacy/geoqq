package postgre

import "errors"

var (
	ErrNoRows           = errors.New("No rows")
	ErrUnexpectedResult = errors.New("Unexpected result")
	ErrUpdateFailed     = errors.New("Update failed")
	ErrInsertFailed     = errors.New("Insert failed")
)
