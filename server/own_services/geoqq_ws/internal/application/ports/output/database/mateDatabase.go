package database

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type MateDatabase interface {
	GetMateIdByChatId(ctx context.Context, userId, chatId uint64) (uint64, error)
	HasMateChatWithId(ctx context.Context, chatId uint64) (bool, error)

	GetMateMessageById(ctx context.Context, mateMessageId uint64) (*domain.MateMessageWithChat, error)
	InsertMateMessage(ctx context.Context, chatId, fromUserId uint64, text string) (uint64, error)

	GetMateChatWithIdForUser(ctx context.Context, userId, chatId uint64) (*domain.MateChat, error)
	GetMateIdsForUser(ctx context.Context, userId uint64) ([]uint64, error)
}
