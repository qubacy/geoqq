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

type ImageStorage struct {
	rwMx        sync.RWMutex
	rootDirName string // "<few more catalogs>/avatar"
	hashManager hash.HashManager
}

func newImageStorage(deps Dependencies) *ImageStorage {
	return &ImageStorage{
		rootDirName: deps.AvatarDirName,
		hashManager: deps.HashManager,
	}
}

// -----------------------------------------------------------------------

func (s *ImageStorage) LoadImage(ctx context.Context, id uint64, ext file.ImageExt) (
	*file.Image, error,
) {
	fileName, _, err := s.fileAndDirNames(id, ext)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}

	// ***

	s.rwMx.RLock()
	defer s.rwMx.RUnlock()

	exists, err := existsFileOrDir(fileName)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}
	if !exists {
		return nil, ErrImageDoesNotExists
	}

	// ***

	bytes, err := os.ReadFile(fileName)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}

	return &file.Image{
		Id:        id,
		Extension: ext,
		Content:   base64.StdEncoding.EncodeToString(bytes),
	}, nil
}

func (s *ImageStorage) SaveImage(ctx context.Context, image *file.Image) error {
	if image == nil {
		return ErrImageIsNil
	}
	if image.Extension.IsValid() {
		return ErrUnknownImageExtension
	}

	// ***

	bytes, err := base64.StdEncoding.DecodeString(image.Content)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	fileName, dirName, err := s.fileAndDirNames(image.Id, image.Extension)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}

	// ***

	s.rwMx.Lock()
	defer s.rwMx.Unlock()

	exists, err := existsFileOrDir(fileName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	if exists {
		return ErrImageAlreadyExists
	}

	// ***

	err = createDirsIfNeeded(dirName)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	file, err := os.Create(fileName) // file descriptor has mode O_RDWR!
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

// private
// -----------------------------------------------------------------------

func (s *ImageStorage) fileNameFromId(id uint64) (string, error) {
	fileName, err := s.hashManager.New(strconv.FormatUint(id, 10))
	if err != nil {
		return "", utility.NewFuncError(s.SaveImage, err)
	}
	return fileName, err
}

func (s *ImageStorage) fileAndDirNames(id uint64, ext file.ImageExt) (string, string, error) {
	fileName, err := s.fileNameFromId(id)
	if err != nil {
		return "", "", utility.NewFuncError(s.fileAndDirNames, err)
	}

	dirName := strings.Join([]string{s.rootDirName, fileName[:2]}, "/")
	fileName = strings.Join([]string{dirName,
		fileName + "." + ext.String()}, "/")

	return fileName, dirName, nil
}

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

func createDirsIfNeeded(dirName string) error {
	exists, err := existsFileOrDir(dirName)
	if err != nil {
		return utility.NewFuncError(createDirsIfNeeded, err)
	}

	if !exists {
		err = os.MkdirAll(dirName, os.ModeDir) // or os.Perm?
		if err != nil {
			return utility.NewFuncError(createDirsIfNeeded, err)
		}
	}
	return nil
}
