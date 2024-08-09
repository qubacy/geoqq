package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type UserIdWithMateMessage struct {
	UserId      uint64
	MateMessage *domain.MateMessageWithChat
}

type MateMessageUsecase interface {
	ForwardMateMessage(ctx context.Context,
		targetUserId uint64, mm *domain.MateMessageWithChat) error

	AddMateMessage(ctx context.Context, userId, chatId uint64, text string) error
	GetFbChansForMateMessages() []<-chan UserIdWithMateMessage // feedback!
}
