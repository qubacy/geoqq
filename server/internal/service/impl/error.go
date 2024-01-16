package impl

import (
	"errors"
	"fmt"
)

var (
	ErrIncorrectUsername              = errors.New("Incorrect username")
	ErrIncorrectPassword              = errors.New("Incorrect password")
	ErrUserWithThisLoginAlreadyExists = errors.New("User with this login already exists")
	ErrIncorrectLoginOrPassword       = errors.New("Incorrect login or password") // No details!
	ErrNotSameHashesForRefreshTokens  = errors.New("Not same hashes for refresh tokens")
)

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("Incorrect username. Pattern `%v`",
		pattern)
}
