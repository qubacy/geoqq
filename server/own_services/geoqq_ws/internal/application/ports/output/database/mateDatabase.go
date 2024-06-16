package database

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type MateDatabase interface {
	GetMateIdByChatId(ctx context.Context, userId, chatId uint64) (uint64, error)

	GetMateMessageById(ctx context.Context, mateMessageId uint64) (*dd.MateMessage, error)
	InsertMateMessage(ctx context.Context, chatId, fromUserId uint64, text string) (uint64, error)
}
