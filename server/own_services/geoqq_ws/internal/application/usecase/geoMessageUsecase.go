package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
)

type GeoMessageUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database

	FbChanCount int
	FbChanSize  int
}

// -----------------------------------------------------------------------

type GeoMessageUsecase struct {
	onlineUsersUc         input.OnlineUsersUsecase
	feedbackChsForGeoMsgs []chan input.UserIdWithGeoMessage

	db database.Database
}

func NewGeoMessageUsecase(params *GeoMessageUcParams) *GeoMessageUsecase {
	feedbackChsForGeoMsgs := []chan input.UserIdWithGeoMessage{}
	for i := 0; i < params.FbChanCount; i++ {
		feedbackChsForGeoMsgs = append(feedbackChsForGeoMsgs,
			make(chan input.UserIdWithGeoMessage, params.FbChanSize))
	}

	// ***

	return &GeoMessageUsecase{
		onlineUsersUc:         params.OnlineUsersUc,
		feedbackChsForGeoMsgs: feedbackChsForGeoMsgs,
		db:                    params.Database,
	}
}

// public
// -----------------------------------------------------------------------

func (g *GeoMessageUsecase) AddGeoMessage(ctx context.Context,
	userId uint64, text string, lon, lat float64) error {
	sourceFunc := g.AddGeoMessage

	geoMessageId, err := g.db.InsertGeoMessage(ctx, userId, text, lon, lat)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	_ = geoMessageId

	return nil
}
