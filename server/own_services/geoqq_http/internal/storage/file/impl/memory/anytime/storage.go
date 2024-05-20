package anytime

import (
	"common/pkg/hash"
	"common/pkg/utility"
)

type Storage struct {
	*ImageStorage
}

type Dependencies struct {
	AvatarDirName string
	HashManager   hash.HashManager
}

func (d *Dependencies) Validate() error {
	if len(d.AvatarDirName) == 0 {
		return ErrRootDirNameIsEmpty
	}
	if d.HashManager == nil {
		return ErrHashManagerIsNil
	}
	return nil
}

// -----------------------------------------------------------------------

func NewStorage(deps Dependencies) (*Storage, error) {
	if err := deps.Validate(); err != nil {
		return nil, utility.NewFuncError(NewStorage, err)
	}

	// ***

	return &Storage{
		ImageStorage: newImageStorage(deps),
	}, nil
}
