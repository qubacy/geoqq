package avatar

import (
	"image"
)

type AvatarGenerator interface {
	New() (image.Image, error)                        // <--- return universal img interface!
	NewFile(dirPath, fileNameWithoutExt string) error // ?

	NewForUser(name string) (image.Image, error)
	NewFileForUser(dirPath, fileNameWithoutExt, name string) error
}
