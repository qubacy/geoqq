package sideError

type Side uint

const (
	Server Side = iota
	Client

	// other sides...
)

type SideError struct {
	Text string
	Side
}

func NewSideError(text string, side Side) *SideError {
	return &SideError{
		Text: text,
		Side: side,
	}
}

func (s *SideError) Error() string {
	return s.Text
}
