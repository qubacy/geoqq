package app

import "errors"

var (
	ErrDomainStorageTypeIsNotDefined error = errors.New("domain storage type is not defined")
	ErrFileStorageTypeIsNotDefined   error = errors.New("file storage type is not defined")
	ErrNotImplemented                      = errors.New("not implemented")
)
