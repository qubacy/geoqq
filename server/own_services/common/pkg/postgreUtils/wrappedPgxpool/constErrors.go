package wrappedPgxpool

import "errors"

var (
	ErrUpdateFailed = errors.New("update failed")
	ErrInsertFailed = errors.New("insert failed")
	ErrDeleteFailed = errors.New("delete failed")
)
