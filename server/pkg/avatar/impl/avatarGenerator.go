package impl

import (
	"image"
	"math/rand"

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

func (a *AvatarGenerator) NewFile(fileNameWithoutExt string) error {
	fileName := fileNameWithoutExt + ".png"
	return govatar.GenerateFile(randomGender(), fileName)
}

// private
// -----------------------------------------------------------------------

func randomGender() govatar.Gender {
	if rand.Int()%2 == 1 {
		return govatar.MALE
	}
	return govatar.FEMALE
}
