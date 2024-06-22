package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/geoDistance"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/cache"
	"geoqq_ws/internal/application/ports/output/database"
	"math/rand"
)

type GeoMessageUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database
	TempDb        cache.Cache

	FbChanCount int
	FbChanSize  int

	MessageLength uint64
	MaxRadius     uint64
	GeoCalculator geoDistance.Calculator
}

// -----------------------------------------------------------------------

type GeoMessageUsecase struct {
	onlineUsersUc         input.OnlineUsersUsecase
	feedbackChsForGeoMsgs []chan input.UserIdWithGeoMessage

	db     database.Database
	tempDb cache.Cache

	messageLength uint64
	maxRadius     uint64
	geoCalculator geoDistance.Calculator
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
		tempDb:                params.TempDb,
		messageLength:         params.MessageLength,
		maxRadius:             params.MaxRadius,
	}
}

// public
// -----------------------------------------------------------------------

func (g *GeoMessageUsecase) AddGeoMessage(ctx context.Context,
	userId uint64, text string, lon, lat float64) error {
	sourceFunc := g.AddGeoMessage

	if len(text) > int(g.messageLength) {
		return ec.New(ErrMessageTooLong(g.messageLength),
			ec.Client, ec.GeoMessageTooLong)
	}
	if err := geoDistance.ValidateLatAndLon(lon, lat); err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.WrongLatOrLon)
	}

	// ***

	var (
		err          error
		geoMessageId uint64
		geoMessage   *domain.GeoMessage
	)

	err = utl.RunFuncsRetErr(
		func() error {
			geoMessageId, err = g.db.InsertGeoMessage(ctx, userId, text, lon, lat)
			return err
		},
		func() error {
			geoMessage, err = g.db.GetGeoMessageWithId(ctx, geoMessageId)
			return err
		})
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	if g.tempDb != nil {
		loc := cache.Location{Lon: lon, Lat: lat}
		userIdAndLocList, err := g.tempDb.SearchUsersWithLocationsNearby(ctx, loc, g.maxRadius)
		if err != nil {
			logger.Error("%v", utl.NewFuncError(g.AddGeoMessage, err))
		}

		for _, userIdAndLoc := range userIdAndLocList {
			messageLoc := geoDistance.Point{Latitude: lat, Longitude: lon}
			userLoc := geoDistance.Point{
				Latitude:  userIdAndLoc.Loc.Lat,
				Longitude: userIdAndLoc.Loc.Lon}

			_ = g.geoCalculator.CalculateDistance(messageLoc, userLoc)

			// TODO: куда-то поместить радиус пользоателя!
		}

		g.sendGeoMessageToFb(1, geoMessage)

	} else {
		logger.Warning(cache.TextCacheDisabled)
	}

	_ = geoMessageId

	return nil
}

func (g *GeoMessageUsecase) GetFbChansForGeoMessages() []<-chan input.UserIdWithGeoMessage {
	chans := []<-chan input.UserIdWithGeoMessage{}
	for i := range g.feedbackChsForGeoMsgs {
		chans = append(chans, g.feedbackChsForGeoMsgs[i]) // copy?
	}

	return chans
}

// private
// -----------------------------------------------------------------------

func (g *GeoMessageUsecase) sendGeoMessageToFb(targetUserId uint64, geoMessage *domain.GeoMessage) {
	if !g.onlineUsersUc.UserIsOnline(targetUserId) {
		return
	}

	count := len(g.feedbackChsForGeoMsgs)
	index := rand.Intn(count)

	g.feedbackChsForGeoMsgs[index] <- input.UserIdWithGeoMessage{
		UserId: targetUserId, MateMsg: geoMessage}
}
