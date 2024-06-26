package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/geoDistance"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"
	"geoqq_ws/internal/application/ports/input/dto"
	"geoqq_ws/internal/application/ports/output/cache"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
)

type UserUcParams struct {
	Database     database.Database
	TempDatabase cache.Cache
}

// -----------------------------------------------------------------------

type UserUsecase struct {
	db     database.Database
	tempDb cache.Cache
}

func NewUserUsecase(params *UserUcParams) *UserUsecase {
	return &UserUsecase{
		db:     params.Database,
		tempDb: params.TempDatabase,
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

	err = u.db.UpdateUserLocation(ctx,
		data.UserId, data.Longitude, data.Latitude) // sync
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// to cache

	if u.tempDb != nil {
		loc := cache.MakeLocation(data.Latitude, data.Longitude)
		if err = u.tempDb.AddUserLocation(ctx, data.UserId, loc); err != nil {
			logger.Error("%v", utl.NewFuncError(sourceFunc, err)) // no critical!
		}
		if err = u.tempDb.AddUserRadius(ctx, data.UserId, data.Radius); err != nil {
			logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		}

	} else {
		logger.Warning(cache.TextCacheDisabled)
	}

	u.db.UpdateBgrLastActionTimeForUser(data.UserId)
	return nil
}

func (u *UserUsecase) GetUserLocation(ctx context.Context, UserId uint64) (
	*domain.UserLocation, error) {

	// TODO: !!

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
