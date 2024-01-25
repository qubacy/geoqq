package file

type ImageExt int

const (
	Unknown ImageExt = iota
	Png
	Jpg
	//...
)

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
	case Jpg:
		return true
	}
	return false
}

// -----------------------------------------------------------------------

type Image struct {
	Id        uint64   `json:"id"`
	Extension ImageExt `json:"ext"`
	Content   string   `json:"content"`
}
