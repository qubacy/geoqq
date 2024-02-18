package impl

import (
	"errors"
	"fmt"
)

var (
	ErrNotImplemented = errors.New("not implemented")

	ErrIncorrectUsername = errors.New("incorrect username")
	ErrIncorrectPassword = errors.New("incorrect password")

	ErrUserWithThisLoginAlreadyExists = errors.New("user with this login already exists")
	ErrIncorrectLoginOrPassword       = errors.New("incorrect login or password") // No details!
	ErrNotSameHashesForRefreshTokens  = errors.New("not same hashes for refresh tokens")

	ErrImageNotFound         = errors.New("image not found")
	ErrUnknownImageExtension = errors.New("unknown image extension")
	ErrImageBodyEmpty        = errors.New("image body empty")
	ErrUserNotFound          = errors.New("user not found")

	ErrMateRequestAlreadySent = errors.New("mate request already sent")
)

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("incorrect username. Pattern `%v`",
		pattern)
}
