package input

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type UserIdWithMateMsg struct {
	UserId  uint64
	MateMsg dd.MateMessage
}

type MateUsecase interface {
	AddMateMessage(ctx context.Context, userId, chatId uint64, text string) error
	GetFbChansForMateMessages() []<-chan UserIdWithMateMsg // feedback!
}
