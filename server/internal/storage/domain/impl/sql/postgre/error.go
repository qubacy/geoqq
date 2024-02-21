package postgre

import "errors"

var (
	ErrNoRows           = errors.New("no rows")
	ErrUnexpectedResult = errors.New("unexpected result")
	ErrUpdateFailed     = errors.New("update failed")
	ErrInsertFailed     = errors.New("insert failed")
)
