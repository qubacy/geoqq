package app

import "errors"

var (
	ErrDomainStorageTypeIsNotDefined error = errors.New("Domain storage type is not defined")
	ErrFileStorageTypeIsNotDefined   error = errors.New("File storage type is not defined")
)
