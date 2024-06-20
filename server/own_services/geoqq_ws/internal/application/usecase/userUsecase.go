package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/geoDistance"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"
	"geoqq_ws/internal/application/ports/input/dto"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
)

type UserUcParams struct {
	Database database.Database
}

// -----------------------------------------------------------------------

type UserUsecase struct {
	Db database.Database
}

func NewUserUsecase(deps UserUcParams) *UserUsecase {
	return &UserUsecase{
		Db: deps.Database,
	}
}

// public
// -----------------------------------------------------------------------

func (u *UserUsecase) UpdateUserLocation(ctx context.Context,
	data dto.UpdateUserLocation) error {

	sourceFunc := u.UpdateUserLocation
	err := validateLatAndLon(data.Longitude, data.Longitude)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = u.Db.UpdateUserLocation(ctx,
		data.UserId, data.Longitude, data.Latitude)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// to redis

	return nil
}

func (u *UserUsecase) GetUserLocation(ctx context.Context, UserId uint64) (
	*domain.UserLocation, error) {

	return nil, constErrors.ErrNotImplemented
}

// private
// -----------------------------------------------------------------------

func validateLatAndLon(longitude, latitude float64) error {
	if err := geoDistance.ValidateLat(latitude); err != nil {
		return ec.New(utl.NewFuncError(validateLatAndLon, err),
			ec.Client, ec.WrongLatitude)
	}

	if err := geoDistance.ValidateLon(longitude); err != nil {
		return ec.New(utl.NewFuncError(validateLatAndLon, err),
			ec.Client, ec.WrongLongitude)
	}

	return nil
}
