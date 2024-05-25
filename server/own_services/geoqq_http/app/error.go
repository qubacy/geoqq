package app

import "errors"

var (
	ErrCacheTypeIsNotDefined         = errors.New("cache type is not defined")
	ErrMsgsTypeIsNotDefined          = errors.New("msgs type is not defined")
	ErrDomainStorageTypeIsNotDefined = errors.New("domain storage type is not defined")
	ErrFileStorageTypeIsNotDefined   = errors.New("file storage type is not defined")
	ErrLoggingTypeIsNotDefined       = errors.New("logging type is not defined")
	ErrNotImplemented                = errors.New("not implemented")
)
