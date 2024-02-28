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

	ErrImageNotFound            = errors.New("image not found")
	ErrUnknownImageExtension    = errors.New("unknown image extension")
	ErrImageBodyEmpty           = errors.New("image body empty")
	ErrUserNotFound             = errors.New("user not found")
	ErrMateRequestNotFound      = errors.New("mate request not found")
	ErrUnknownMateRequestResult = errors.New("unknown mate request result")

	ErrMateRequestAlreadySentFromYou = errors.New("mate request already sent from you")
	ErrMateRequestAlreadySentToYou   = errors.New("mate request already sent to you")
	ErrMateRequestNotWaiting         = errors.New("mate request not waiting")
	ErrAlreadyAreMates               = errors.New("already are mates")
	ErrMateRequestToSelf             = errors.New("mate request to self")
)

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("incorrect username. Pattern `%v`",
		pattern)
}
