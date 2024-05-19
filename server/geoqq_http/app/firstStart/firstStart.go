package firstStart

import (
	"context"
	"errors"
	"geoqq_http/pkg/file"
	"geoqq_http/pkg/hash"
	"geoqq_http/pkg/logger"
	"geoqq_http/pkg/utility"
	"io"
	"os"
	"path/filepath"
	"strings"

	domainStorage "geoqq_http/internal/storage/domain"
	fileStorage "geoqq_http/internal/storage/file"
)

func InsertDataIntoStorages(
	ctxForInit context.Context,
	ds domainStorage.Storage,
	fs fileStorage.Storage,
	hashManager hash.HashManager,
) error {
	sourceFunc := InsertDataIntoStorages

	has, err := ds.HasAvatarsWithLabel(ctxForInit, domainStorage.LabelDeletedUser)
	if err != nil {
		return utility.NewFuncError(sourceFunc, err)
	}
	if !has {
		if err := insertAvatarsForDeletedUsers(
			ctxForInit, ds, fs, hashManager); err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}

		logger.Info("avatars for deleted users are inserted")
	}

	// next steps...

	return nil
}

// private
// -----------------------------------------------------------------------

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

		imageBytes, err := readImageContent(
			strings.Join([]string{pathToImages, dirEntry.Name()},
				string(os.PathSeparator)),
		)
		if err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}
		imageHash, err := hashManager.NewFromBytes(imageBytes)
		if err != nil {
			return utility.NewFuncError(sourceFunc, err)
		}

		// ***

		id, err := ds.InsertServerGeneratedAvatarWithLabel(ctxForInit,
			imageHash, domainStorage.LabelDeletedUser)
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

func readImageContent(fn string) ([]byte, error) {
	f, err := os.Open(fn)
	if err != nil {
		return nil, ErrFailedToOpenFile
	}

	imageBytes, err := io.ReadAll(f)
	if err != nil {
		return nil, ErrFailedToReadFile
	}
	return imageBytes, nil
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
