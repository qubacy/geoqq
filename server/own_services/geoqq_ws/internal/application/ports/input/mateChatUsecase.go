package input

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
)

type UserIdWithMateChat struct {
	UserId   uint64
	MateChat *dd.MateChat
}

type MateChatUsecase interface {
	InformAboutMateChatUpdated(ctx context.Context, targetUserId, mateChatId uint64) error
	InformAboutMateChatAdded(ctx context.Context, targetUserId, mateChatId uint64) error
}
