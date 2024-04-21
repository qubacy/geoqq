package app

import "errors"

var (
	ErrDomainStorageTypeIsNotDefined = errors.New("domain storage type is not defined")
	ErrFileStorageTypeIsNotDefined   = errors.New("file storage type is not defined")
	ErrLoggingTypeIsNotDefined       = errors.New("logging type is not defined")
	ErrNotImplemented                = errors.New("not implemented")
)
