package input

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type UserIdWithMateMessage struct {
	UserId      uint64
	MateMessage *dd.MateMessage
}

type MateMessageUsecase interface {
	AddMateMessage(ctx context.Context, userId, chatId uint64, text string) error
	GetFbChansForMateMessages() []<-chan UserIdWithMateMessage // feedback!
}
