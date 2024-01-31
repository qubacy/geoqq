package anytime

import (
	"errors"
)

var (
	ErrImageIsNil              = errors.New("Image is nil")
	ErrImageAlreadyExists      = errors.New("Image already exists")
	ErrImageDoesNotExists      = errors.New("Image does not exists")
	ErrUnknownImageExtension   = errors.New("Unknown image extension")
	ErrRootDirNameIsEmpty      = errors.New("Root directory name is empty")
	ErrHashManagerIsNil        = errors.New("Hash manager is nil")
	ErrImageCountNotEqualToOne = errors.New("Number of images not equal to one")
)
