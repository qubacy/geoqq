package anytime

import (
	"geoqq/pkg/hash"
)

type Storage struct {
	*ImageStorage
}

type Dependencies struct {
	AvatarDirName string
	HashManager   hash.HashManager
}

func NewStorage(deps Dependencies) (*Storage, error) {
	if len(deps.AvatarDirName) == 0 {
		return nil, ErrRootDirNameIsEmpty
	}
	if deps.HashManager == nil {
		return nil, ErrHashManagerIsNil
	}

	// ***

	return &Storage{
		ImageStorage: newImageStorage(deps),
	}, nil
}
