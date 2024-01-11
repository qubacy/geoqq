package sideError

import (
	"errors"
	"fmt"
)

type Side string

const (
	Server Side = "server"
	Client      = "client"

	// other sides...
)

func New(text string, side Side) error {
	err := errors.New(string(side))
	return fmt.Errorf("on side: %w", err)
}

// -----------------------------------------------------------------------

func UnwrapToLast(err error) error {
	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	return err
}

func UnwrapToSide(err error) error {
	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	return err
}
