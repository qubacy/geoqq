package anytime

import (
	"context"
	"encoding/base64"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	"geoqq/pkg/utility"
	"os"
	"strconv"
	"strings"
	"sync"
)

type Storage struct {
	rwMx        sync.RWMutex
	rootDirName string // "<catalog name>/avatar"
	hashManager hash.HashManager
}

type Dependencies struct {
	RootDirName string
	HashManager hash.HashManager
}

func NewStorage(deps Dependencies) (*Storage, error) {
	if len(deps.RootDirName) == 0 {
		return nil, ErrRootDirNameIsEmpty
	}
	if deps.HashManager == nil {
		return nil, ErrHashManagerIsNil
	}

	return &Storage{
		rootDirName: deps.RootDirName,
		hashManager: deps.HashManager,
	}, nil
}

// -----------------------------------------------------------------------

func (s *Storage) LoadImage(ctx context.Context, id uint64, ext file.ImageExt) (*file.Image, error) {
	fileName, err := s.hashManager.New(strconv.FormatUint(id, 10))
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}

	subDirName := strings.Join([]string{s.rootDirName, fileName[:2]}, "/")
	fileName = strings.Join([]string{subDirName, fileName + "." + ext.String()}, "/")

	// ***

	exists, err := existsFileOrDir(fileName)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}
	if !exists {
		return nil, ErrImageDoesNotExists
	}

	imageFile, err := os.Open(fileName)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}

	// TODO: read and convert!

	return nil, nil
}

func (s *Storage) SaveImage(ctx context.Context, image *file.Image) error {
	if image == nil {
		return ErrImageIsNil
	}

	// ***

	bytes, err := base64.StdEncoding.DecodeString(image.Content)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	fileName, err := s.hashManager.New(strconv.FormatUint(image.Id, 10))
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}

	// ***

	if image.Extension.IsValid() {
		return ErrUnknownImageExtension
	}

	subDirName := strings.Join([]string{s.rootDirName, fileName[:2]}, "/")
	fileName = strings.Join([]string{subDirName, fileName + "." + image.Extension.String()}, "/")

	exists, err := existsFileOrDir(fileName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	if exists {
		return ErrImageAlreadyExists
	}

	// ***

	err = createDirIfNeeded(subDirName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	file, err := os.Create(fileName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	defer file.Close()

	_, err = file.Write(bytes)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	return nil
}

// -----------------------------------------------------------------------

func existsFileOrDir(path string) (bool, error) {
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}

	if os.IsNotExist(err) {
		return false, nil
	}

	return false, utility.NewFuncError(
		existsFileOrDir, err)
}

func createDirIfNeeded(dirName string) error {
	exists, err := existsFileOrDir(dirName)
	if err != nil {
		return utility.NewFuncError(createDirIfNeeded, err)
	}

	if !exists {
		err = os.Mkdir(dirName, os.ModeDir)
		if err != nil {
			return utility.NewFuncError(createDirIfNeeded, err)
		}
	}
	return nil
}
