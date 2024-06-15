package database

import "context"

type MateDatabase interface {
	InsertMateMessage(ctx context.Context, chatId uint64,
		fromUserId uint64, text string) (uint64, error)
}
