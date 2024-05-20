package impl

import (
	"common/pkg/utility"
	"image"
	"math/rand"
	"os"

	"github.com/o1egl/govatar"
)

type AvatarGenerator struct{}

func NewAvatarGenerator() (*AvatarGenerator, error) {
	return &AvatarGenerator{}, nil
}

// -----------------------------------------------------------------------

func (a *AvatarGenerator) New() (image.Image, error) {
	img, err := govatar.Generate(randomGender())
	if err != nil {
		return nil, err
	}

	return img, err
}

func (a *AvatarGenerator) NewFile(dirPath, fileNameWithoutExt string) error {
	err := os.MkdirAll(dirPath, os.ModeDir)
	if err != nil {
		return utility.NewFuncError(a.NewFile, err)
	}

	fileName := dirPath + "/" + fileNameWithoutExt + ".png"
	return govatar.GenerateFile(randomGender(), fileName)
}

// -----------------------------------------------------------------------

func (a *AvatarGenerator) NewForUser(name string) (image.Image, error) {
	img, err := govatar.GenerateForUsername(randomGender(), name)
	if err != nil {
		return nil, err
	}

	return img, err
}

func (a *AvatarGenerator) NewFileForUser(dirPath, fileNameWithoutExt, name string) error {
	err := os.MkdirAll(dirPath, os.ModeDir)
	if err != nil {
		return utility.NewFuncError(a.NewFile, err)
	}

	fileName := dirPath + "/" + fileNameWithoutExt + ".png"
	return govatar.GenerateFileForUsername(randomGender(), name, fileName)
}

// private
// -----------------------------------------------------------------------

func randomGender() govatar.Gender {
	if rand.Int()%2 == 1 {
		return govatar.MALE
	}
	return govatar.FEMALE
}
