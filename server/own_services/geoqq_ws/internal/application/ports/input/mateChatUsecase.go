package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type UserIdWithMateChat struct {
	UserId   uint64
	MateChat *domain.MateChat
}

type MateChatUsecase interface {
	InformAboutMateChatUpdated(ctx context.Context, targetUserId, mateChatId uint64) error
	InformAboutMateChatAdded(ctx context.Context, targetUserId, mateChatId uint64) error
}
