package firstStart

import "errors"

var (
	ErrUnexpectedItemInDir   = errors.New("unexpected item in directory")
	ErrUnknownImageExtension = errors.New("unknown image extension")
	ErrFailedToOpenFile      = errors.New("failed to open file")
	ErrFailedToReadFile      = errors.New("failed to read file")
)
