package impl

import (
	"errors"
	"fmt"
)

const (
	Server uint = iota
	Client

	// other sides...
)

// -----------------------------------------------------------------------

type SideError struct {
	side uint
	err  error // with text!

	// clientId int
}

func NewSideError(err error, side uint) *SideError {
	return &SideError{
		err:  err,
		side: side,
	}
}

func New(err error, side uint) error {
	return NewSideError(err, side)
}

// -----------------------------------------------------------------------

func (s *SideError) Unwrap() error {
	return s.err
}

func (s *SideError) UnwrapToLast() error {
	err := s.err
	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	return err
}

func (s *SideError) Side() uint {
	return s.side
}

func (s *SideError) Error() string {
	return fmt.Sprintf("%v\n on side: %v",
		s.err, s.side)
}

// unwind errors
// -----------------------------------------------------------------------

func UnwrapErrorsToLastSide(err error) *SideError {
	var lastSideError *SideError = nil
	for err != nil {
		possibleSideError, converted := err.(*SideError)
		if converted {
			lastSideError = possibleSideError
		}

		err = errors.Unwrap(err)
	}
	return lastSideError
}

func UnwrapErrorsToSide(err error) uint {
	sideErr := UnwrapErrorsToLastSide(err)
	if sideErr == nil {
		return Server
	}

	return sideErr.side
}
