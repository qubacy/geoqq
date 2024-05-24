package usecase

import (
	"common/pkg/geoDistance"
	"common/pkg/utility"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/inputPort/dto"
	"geoqq_ws/internal/application/outputPort/database"
)

type UserUsecase struct {
	Db database.Database
}

func newUserUsecase(deps Dependencies) *UserUsecase {
	return &UserUsecase{
		Db: deps.Database,
	}
}

// public
// -----------------------------------------------------------------------

func (u *UserUsecase) UpdateUserLocation(ctx context.Context,
	data dto.UpdateUserLocation) error {

	err := geoDistance.ValidateLatAndLon(data.Longitude, data.Longitude)
	if err != nil {
		return utl.NewFuncError(u.UpdateUserLocation, err)
	}

	// ***

	err := u.Db.InsertUserLocation(ctx, data.Longitude,
		data.Latitude, data.Radius)
	if err != nil {

		// TODO:
		return utility.NewFuncError(u.AddUserLocation, err)
	}

	return nil
}

// private
// -----------------------------------------------------------------------

func validateLatAndLon(longitude, latitude float64) error {

}
