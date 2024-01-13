package impl

import (
	"errors"
	"fmt"
)

var (
	ErrIncorrectUsername = errors.New("Incorrect username")
	ErrIncorrectPassword = errors.New("Incorrect password")
)

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("Incorrect username. Pattern `%v`",
		pattern)
}
