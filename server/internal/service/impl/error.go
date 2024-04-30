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
	ErrIncorrectLoginOrPassword       = errors.New("incorrect login or password") // no details?
	ErrUserWithNameHasBeenDeleted     = errors.New("user with name has been deleted")
	ErrTargetUserDeleted              = errors.New("target user deleted")
	ErrNotSameHashesForRefreshTokens  = errors.New("not same hashes for refresh tokens")

	ErrAddImageBlocked         = errors.New("add image blocked")
	ErrImageNotFound           = errors.New("image not found")
	ErrOneOrMoreImagesNotFound = errors.New("one or more images not found")
	ErrUnknownImageExtension   = errors.New("unknown image extension")
	ErrImageContentEmpty       = errors.New("image content empty")

	ErrUserNotFound             = errors.New("user not found")
	ErrOneOrMoreUsersNotFound   = errors.New("one or more users not found")
	ErrMateRequestNotFound      = errors.New("mate request not found")
	ErrUnknownMateRequestResult = errors.New("unknown mate request result")

	ErrMateChatNotFound         = errors.New("mate chat not found")
	ErrMateChatNotAvailable     = errors.New("mate chat not available for user")
	ErrCountMoreThanPermissible = errors.New("count is more than permissible")

	ErrMateRequestAlreadySentFromYou = errors.New("mate request already sent from you")
	ErrMateRequestAlreadySentToYou   = errors.New("mate request already sent to you")
	ErrMateRequestNotWaiting         = errors.New("mate request not waiting")
	ErrAlreadyAreMates               = errors.New("already are mates")
	ErrMateRequestToSelf             = errors.New("mate request to self")

	ErrWrongLongitude = errors.New("wrong longitude")
	ErrWrongLatitude  = errors.New("wrong latitude")

	ErrInputParameterIsNil = errors.New("input parameter is nil")
)

// with params
// -----------------------------------------------------------------------

func ErrSignInByNameIsBlocked(name string) error {
	return fmt.Errorf("sign in by `%v` is blocked", name)
}

func ErrSignUpByIpAddrBlocked(ipAddr string) error {
	return fmt.Errorf("sign up by ip `%v` is blocked", ipAddr)
}

func ErrIncorrectUsernameWithPattern(pattern string) error {
	return fmt.Errorf("incorrect username. Pattern `%v`", pattern)
}

func ErrAddImageBlockedForUserWithId(userId uint64) error {
	return fmt.Errorf("add image blocked for user with id `%v`", userId)
}

func ErrMessageTooLong(maxMsgLength uint64) error {
	return fmt.Errorf("message is too long. Max length: %v", maxMsgLength)
}

func ErrImageWithIdNotInCache(id uint64) error {
	return fmt.Errorf("image %v is not in cache", id)
}
