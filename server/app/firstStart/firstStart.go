package firstStart

import (
	"context"
	"errors"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	"geoqq/pkg/logger"
	"geoqq/pkg/utility"
	"io"
	"os"
	"path/filepath"
	"strings"

	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
)

func InsertDataIntoStorages(
	ctxForInit context.Context,
	ds domainStorage.Storage,
	fs fileStorage.Storage,
	hashManager hash.HashManager,
) error {
	sourceFunc := InsertDataIntoStorages

	// TODO: check

	err := insertAvatarsForDeletedUsers(ctxForInit,
		ds, fs, hashManager)
	if err != nil {
		return utility.NewFuncError(sourceFunc, err)
	}

	return nil
}

func pathToAssets() (string, error) {
	wd, err := os.Getwd()
	if err != nil {
		return "", utility.NewFuncError(pathToAssets, err)
	}

	parts := strings.Split(wd, string(os.PathSeparator))
	parts = parts[:len(parts)-1] // remove app!
	parts = append(parts, "assets")
	return strings.Join(parts, string(os.PathSeparator)), nil
}

// -----------------------------------------------------------------------

const (
	labelDeletedUser = "deletedUser"
)

func insertAvatarsForDeletedUsers(
	ctxForInit context.Context,
	ds domainStorage.Storage,
	fs fileStorage.Storage,
	hashManager hash.HashManager,
) error {
	sourceFunc := insertAvatarsForDeletedUsers
	pathToImages, err := pathToAssets()
	if err != nil {
		return utility.NewFuncError(pathToAssets, err)
	}
	pathToImages = strings.Join(
		[]string{pathToImages, "firstData", "deletedUser"}, // permanent dirs!
		string(os.PathSeparator),
	)
	logger.Debug("path to images for deleted users: %v",
		pathToImages)

	dirEntries, err := os.ReadDir(pathToImages)
	if err != nil {
		return utility.NewFuncError(sourceFunc, err)
	}
	for i, dirEntry := range dirEntries {
		logger.Debug("dir entry [%v] has name %v", i, dirEntry.Name())
		if dirEntry.IsDir() {
			return ErrUnexpectedItemInDir
		}
		strFileExtension := filepath.Ext(dirEntry.Name())[1:] // remove dot...
		ext := file.MakeImageExtFromString(strFileExtension)
		if ext == file.Unknown {
			return ErrUnknownImageExtension
		}

		// ***

		imageBytes, err := readImageContent(pathToImages, dirEntry.Name())
		if err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}
		imageHash, err := hashManager.NewFromBytes(imageBytes)
		if err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}

		// ***

		id, err := ds.InsertServerGeneratedAvatarWithLabel(ctxForInit,
			imageHash, labelDeletedUser)
		if err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}

		image := file.NewImageFromBytes(id, ext, imageBytes)
		err = fs.SaveImage(ctxForInit, image)
		if err != nil {
			err = errors.Join(err, ds.DeleteAvatarWithId(ctxForInit, id))
			return utility.NewFuncError(sourceFunc, err)
		}
	}

	return nil
}

func readImageContent(pathToImages, fn string) ([]byte, error) {
	f, err := os.Open(pathToImages + string(os.PathSeparator) + fn)
	if err != nil {
		return nil, ErrFailedToOpenFile
	}

	imageBytes, err := io.ReadAll(f)
	if err != nil {
		return nil, ErrFailedToReadFile
	}
	return imageBytes, nil
}
