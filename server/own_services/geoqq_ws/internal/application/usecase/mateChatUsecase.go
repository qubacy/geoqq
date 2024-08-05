package usecase

import (
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
)

type MateChatUsecase struct {
	onlineUsersUc input.OnlineUsersUsecase
	db            database.Database
}

func NewMateChatUsecase(
	onlineUsersUc input.OnlineUsersUsecase,
	db database.Database) *MateChatUsecase {

	return &MateChatUsecase{
		onlineUsersUc: onlineUsersUc,
		db:            db,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateChatUsecase) InformAboutMateChatUpdated(ctx context.Context,
	targetUserId, mateChatId uint64) error {

	return constErrors.ErrNotImplemented
}

func (m *MateChatUsecase) InformAboutMateChatAdded(ctx context.Context,
	targetUserId, mateChatId uint64) error {

	return constErrors.ErrNotImplemented
}
