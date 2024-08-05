package usecase

import (
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
)

type PublicUserUsecase struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database
}

func NewPublicUserUsecase(
	onlineUsersUc input.OnlineUsersUsecase,
	db database.Database,
) *PublicUserUsecase {
	return &PublicUserUsecase{
		OnlineUsersUc: onlineUsersUc,
		Database:      db,
	}
}

// -----------------------------------------------------------------------

func (p *PublicUserUsecase) InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error {

	return constErrors.ErrNotImplemented
}
