package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
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

/*
userId - user who updated their account!
*/
func (p *PublicUserUsecase) InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error {
	sourceFunc := p.InformAboutPublicUserUpdated
	mateIds, err := p.Database.GetMateIdsForUser(ctx, userId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	return constErrors.ErrNotImplemented
}
