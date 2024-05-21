package usecase

import (
	"common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/inputPort/dto"
	"geoqq_ws/internal/application/outputPort/database"
)

type UserUsecase struct {
	Db database.Database
}

func newUserUsecase(deps Dependencies) *UserUsecase {
	return &UserUsecase{
		Db: deps.Db,
	}
}

// public
// -----------------------------------------------------------------------

func (u *UserUsecase) AddUserLocation(ctx context.Context,
	data dto.AddUserLocation) error {

	// ***

	err := u.Db.InsertUserLocation(ctx, data.Longitude,
		data.Latitude, data.Radius)
	if err != nil {

		// TODO:
		return utility.NewFuncError(u.AddUserLocation, err)
	}

	return nil
}
