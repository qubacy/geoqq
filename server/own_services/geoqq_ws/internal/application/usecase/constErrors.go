package usecase

import (
	"fmt"
)

func ErrMessageTooLong(maxMsgLength uint64) error {
	return fmt.Errorf("message is too long. Max length: %v", maxMsgLength)
}
