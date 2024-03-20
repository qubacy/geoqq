package file

import "encoding/base64"

type ImageExt int

const (
	Unknown ImageExt = iota
	Png
	Jpg
	//...
)

// -----------------------------------------------------------------------

func MakeImageExtFromString(value string) ImageExt {
	switch value {
	case "png":
		return Png
	case "jpg":
		return Jpg
	}
	return Unknown
}

func (ie ImageExt) String() string {
	switch ie {
	case Png:
		return "png"
	case Jpg:
		return "jpg"
	}

	return "unknown"
}

func (ie ImageExt) IsValid() bool {
	switch ie {
	case Png:
		return true
	case Jpg:
		return true
	}
	return false
}

// -----------------------------------------------------------------------

// serializable?
type Image struct {
	Id        uint64   `json:"id"`
	Extension ImageExt `json:"ext"`
	Content   string   `json:"content"` // base64!
}

func NewImageWithoutId(ext ImageExt, contentAsBase64 string) *Image {
	return &Image{
		Extension: ext,
		Content:   contentAsBase64,
	}
}

func NewPngImageFromBytes(id uint64, bytes []byte) *Image {
	return &Image{
		Id:        id,
		Extension: Png,
		Content:   base64.StdEncoding.EncodeToString(bytes),
	}
}

type Images struct {
	ImageList []*Image `json:"images"`
}

func NewImages(imageList []*Image) *Images {
	return &Images{
		ImageList: imageList,
	}
}
