package input

import (
	"context"
)

type MateUsecase interface {
	AddMateMessage(ctx context.Context,
		chatId uint64, text string) error
}
