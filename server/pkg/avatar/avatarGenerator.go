package avatar

import "image"

type AvatarGenerator interface {
	New() (image.Image, error)
	NewFile(fileNameWithoutExt string) error // ?
}
