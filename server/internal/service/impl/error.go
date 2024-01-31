package impl

import (
	"errors"
	"fmt"
)

var (
	ErrNotImplemented = errors.New("Not implemented")

	ErrIncorrectUsername = errors.New("Incorrect username")
	ErrIncorrectPassword = errors.New("Incorrect password")

	ErrUserWithThisLoginAlreadyExists = errors.New("User with this login already exists")
	ErrIncorrectLoginOrPassword       = errors.New("Incorrect login or password") // No details!
	ErrNotSameHashesForRefreshTokens  = errors.New("Not same hashes for refresh tokens")

	ErrImageNotFound = errors.New("Image not found")
)

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("Incorrect username. Pattern `%v`",
		pattern)
}
