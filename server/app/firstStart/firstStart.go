package firstStart

import (
	"context"
	"geoqq/pkg/file"
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
	ds domainStorage.Storage,
	fs fileStorage.Storage,
) error {
	sourceFunc := InsertDataIntoStorages
	wd, err := os.Getwd()
	if err != nil {
		return utility.NewFuncError(sourceFunc, err)
	}

	parts := strings.Split(wd, string(os.PathSeparator))
	parts = parts[:len(parts)-1] // remove app!
	parts = append(parts, "assets", "firstData", "deletedProfile")
	pathToFirstData := strings.Join(parts, string(os.PathSeparator))
	logger.Debug("path to first data: %v", pathToFirstData)

	dirEntries, err := os.ReadDir(pathToFirstData)
	if err != nil {
		return utility.NewFuncError(sourceFunc, err)
	}

	for i, dirEntry := range dirEntries {
		logger.Debug("dir entry [%v] has name %v", i, dirEntry.Name())
		if dirEntry.IsDir() {
			return ErrUnexpected
		}

		strFileExtension := filepath.Ext(dirEntry.Name()) // remove dot
		ext := file.MakeImageExtFromString(strFileExtension[1:])
		if ext == file.Unknown {
			return ErrUnexpected
		}

		id, err := ds.InsertServerGeneratedAvatarWithLabel(context.Background(), "", "deletedProfile")
		if err != nil {
			return ErrUnexpected
		}

		f, err := os.Open(pathToFirstData + string(os.PathSeparator) + dirEntry.Name())
		if err != nil {
			return ErrUnexpected
		}
		imageBytes, err := io.ReadAll(f)
		if err != nil {
			return ErrUnexpected
		}
		image := file.NewImageFromBytes(id, ext, imageBytes)
		err = fs.SaveImage(context.Background(), image)
		if err != nil {
			return ErrUnexpected
		}
	}

	return nil
}
