package anytime

import (
	"common/pkg/file"
	"common/pkg/hash"
	"common/pkg/utility"
	"context"
	"encoding/base64"
	"os"
	"path/filepath"
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

func (s *ImageStorage) HasImage(ctx context.Context, id uint64) (bool, error) {
	return false, nil
}

func (s *ImageStorage) LoadImage(ctx context.Context, id uint64) (
	*file.Image, error,
) {
	fileName, _, err := s.wholeFileAndDirNames(id)
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}
	matches, err := filepath.Glob(fileName + ".?*") // will be more than 1 char
	if err != nil {
		return nil, utility.NewFuncError(s.LoadImage, err)
	}

	if len(matches) != 1 {
		return nil, ErrImageCountNotEqualToOne // image not found!
	}

	// ***

	s.rwMx.RLock()
	defer s.rwMx.RUnlock()

	fileName = matches[0]
	extStr := fileName[strings.LastIndex(fileName, ".")+1:]
	ext := file.MakeImageExtFromString(extStr)
	if !ext.IsValid() {
		return nil, ErrUnknownImageExtension // unexpected!
	}

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
	if !image.Extension.IsValid() {
		return ErrUnknownImageExtension
	}

	// ***

	bytes, err := base64.StdEncoding.DecodeString(image.Content)
	if err != nil {
		return utility.NewFuncError(s.SaveImage, err)
	}
	fileName, dirName, err := s.wholeFileWithExtAndDirNames(image.Id, image.Extension)
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

func (s *ImageStorage) fileNameFromId(id uint64) string {
	fileName := strconv.FormatUint(id, 10)
	return fileName
}

func (s *ImageStorage) wholeFileWithExtAndDirNames(id uint64, ext file.ImageExt) (string, string, error) {
	fileName, dirName, err := s.wholeFileAndDirNames(id)
	if err != nil {
		return "", "", utility.NewFuncError(s.wholeFileWithExtAndDirNames, err)
	}

	return fileName + "." + ext.String(), dirName, nil
}

func (s *ImageStorage) wholeFileAndDirNames(id uint64) (string, string, error) {
	fileName := s.fileNameFromId(id)
	fileNameHash, err := s.hashManager.NewFromString(fileName)
	if err != nil {
		return "", "", utility.NewFuncError(s.wholeFileAndDirNames, err)
	}

	dirName := strings.Join([]string{s.rootDirName, fileNameHash[:2]}, "/")
	fileName = strings.Join([]string{dirName, fileName}, "/")

	return fileName, dirName, nil
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
