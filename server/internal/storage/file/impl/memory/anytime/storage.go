package anytime

import (
	"context"
	"encoding/base64"
	"fmt"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	"geoqq/pkg/utility"
	"os"
	"strconv"
)

type Storage struct {
	rootDirectoryName string // "<catalog name>/avatar"
	hashManager       hash.HashManager
}

type Dependencies struct {
	RootDirectoryName string
	HashManager       hash.HashManager
}

func NewStorage(deps Dependencies) (*Storage, error) {
	if len(deps.RootDirectoryName) == 0 {
		return nil, fmt.Errorf("Root directory name is empty")
	}
	if deps.HashManager == nil {
		return nil, fmt.Errorf("Hash manager is nil")
	}

	return &Storage{
		rootDirectoryName: deps.RootDirectoryName,
		hashManager:       deps.HashManager,
	}, nil
}

// -----------------------------------------------------------------------

func (s *Storage) LoadImage(ctx context.Context, id uint64) (file.Image, error) {
	return file.Image{}, nil
}

func (s *Storage) SaveImage(ctx context.Context, image file.Image) error {
	_, err := base64.StdEncoding.DecodeString(image.Content)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}

	fileName, err := s.hashManager.New(strconv.FormatUint(image.Id, 10))
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}

	subDirName := s.wholeSubDirectoryName(fileName[:2])
	fileName = fileName[2:]

	exists, err := existsDirectory(subDirName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}

	if !exists {
		err = os.Mkdir(subDirName, os.ModeDir)
		if err != nil {
			return utility.NewFuncError(s.SaveImage, err)
		}
	}

	return nil
}

// -----------------------------------------------------------------------

func (s *Storage) wholeSubDirectoryName(subDirName string) string {
	return s.rootDirectoryName + "/" + subDirName
}

func existsDirectory(path string) (bool, error) {
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}

	if os.IsNotExist(err) {
		return false, nil
	}

	return false, err
}
