package anytime

import (
	"errors"
)

var (
	ErrImageIsNil              = errors.New("image is nil")
	ErrImageAlreadyExists      = errors.New("image already exists")
	ErrImageDoesNotExists      = errors.New("image does not exists")
	ErrUnknownImageExtension   = errors.New("unknown image extension")
	ErrRootDirNameIsEmpty      = errors.New("root directory name is empty")
	ErrHashManagerIsNil        = errors.New("hash manager is nil")
	ErrImageCountNotEqualToOne = errors.New("number of images not equal to one")
)
