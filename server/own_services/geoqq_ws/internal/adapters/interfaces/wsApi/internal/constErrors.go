package internal

import (
	"errors"
	"fmt"
)

var (
	ErrSessionStorageHasNoUserId   = errors.New("session storage has no user id")
	ErrUserIdNotConvertedToUint64  = errors.New("user id not converted to uint64")
	ErrClientNotFoundBySocketInMap = errors.New("client not found by socket in map")
	ErrSocketNotFoundByUserIdInMap = errors.New("socket not found by user id in map")

	ErrUnknownAction        = errors.New("unknown action")
	ErrUserIdChangedForConn = errors.New("user id changed for connection")
)

func ErrUnknownActionWithName(name string) error {
	return fmt.Errorf("unknown action `%v`", name)
}

func ErrSocketNotFoundByUserIdInMapWith(userId uint64) error {
	return fmt.Errorf("socket not found by user id %v in map", userId)
}
