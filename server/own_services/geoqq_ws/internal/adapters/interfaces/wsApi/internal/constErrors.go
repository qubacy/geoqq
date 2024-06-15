package internal

import (
	"errors"
	"fmt"
)

var (
	ErrSessionStorageHasNoUserId   = errors.New("session storage has no user id")
	ErrUserIdNotConvertedToUint64  = errors.New("user id not converted to uint64")
	ErrClientNotFoundBySocketInMap = errors.New("client not found by socket in map")
	ErrUnknownAction               = errors.New("unknown action")
	ErrUserIdChangedForConn        = errors.New("user id changed for connection")
)

func ErrUnknownActionWithName(name string) error {
	return fmt.Errorf("unknown action `%v`", name)
}
