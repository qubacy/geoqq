package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	geo "common/pkg/geoDistance"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	dd "geoqq_ws/internal/application/domain"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/cache"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
	"math/rand"
)

type GeoMessageUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database
	TempDatabase  cache.Cache

	FbChanCount int
	FbChanSize  int

	MaxMessageLength uint64
	MaxRadius        uint64
	GeoCalculator    geo.Calculator
}

// -----------------------------------------------------------------------

type GeoMessageUsecase struct {
	onlineUsersUc         input.OnlineUsersUsecase
	feedbackChsForGeoMsgs []chan input.UserIdWithGeoMessage

	db     database.Database
	tempDb cache.Cache

	maxMessageLength uint64
	maxRadius        uint64
	geoCalculator    geo.Calculator
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
		tempDb:                params.TempDatabase,
		maxMessageLength:      params.MaxMessageLength,
		maxRadius:             params.MaxRadius, // meters
		geoCalculator:         params.GeoCalculator,
	}
}

// public
// -----------------------------------------------------------------------

func (g *GeoMessageUsecase) ForwardGeoMessage(ctx context.Context,
	gm *dd.GeoMessage, lon, lat float64) error {
	/*
		Geo message has already
			been added to the database!
	*/

	if gm == nil {
		return constErrors.ErrInputParamWithTypeNotSpecified(`*dd.GeoMessage`)
	}

	if err := g.sendGeoMessageToWhoeverNeeds(ctx, gm, lon, lat); err != nil {
		logger.Error("%v", utl.NewFuncError(g.ForwardGeoMessage, err))

		return nil // ?
	}

	return nil
}

func (g *GeoMessageUsecase) AddGeoMessage(ctx context.Context,
	userId uint64, text string, lon, lat float64) error {
	sourceFunc := g.AddGeoMessage

	if len(text) > int(g.maxMessageLength) {
		return ec.New(ErrMessageTooLong(g.maxMessageLength),
			ec.Client, ec.GeoMessageTooLong)
	}
	if err := geo.ValidateLatAndLon(lon, lat); err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.WrongLatOrLon)
	}

	// ***

	var (
		err          error
		geoMessageId uint64
		geoMessage   *dd.GeoMessage
	)

	if geoMessageId, err = g.db.InsertGeoMessage(ctx, userId, text, lon, lat); err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err), ec.Server, ec.DomainStorageError)
	}
	geoMessage = dd.NewGeoMessage(geoMessageId, userId, text)

	// ***

	if err = g.sendGeoMessageToWhoeverNeeds(ctx, geoMessage, lon, lat); err != nil {
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))

		return nil // ?
	}

	// ***

	g.db.UpdateBgrLocationForUser(userId, lon, lat)
	g.db.UpdateBgrLastActionTimeForUser(userId)

	return nil // ok
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

func (g *GeoMessageUsecase) sendGeoMessageToWhoeverNeeds(ctx context.Context,
	geoMessage *dd.GeoMessage, lon, lat float64) error {
	sourceFunc := g.sendGeoMessageToWhoeverNeeds

	if g.tempDb != nil { // like it's not a necessary part?
		messageLocation := cache.MakeLocation(lat, lon)
		userIdWithLocationMap, err := g.tempDb.SearchUsersWithLocationsNearby(
			ctx, messageLocation, g.maxRadius)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		userIds := cache.ToKeys(userIdWithLocationMap)
		userIdWithRadiusMap, err := g.tempDb.GetUserRadiuses(ctx, userIds...) // if exists!
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		for userId, loc := range userIdWithLocationMap {
			messageLoc := geo.MakePoint(loc.Lat, loc.Lon)
			userLoc := loc.ToGeoPoint()

			userDesiredRadius := float64(userIdWithRadiusMap[userId])
			currentRadius := g.geoCalculator.CalculateDistance(messageLoc, userLoc)

			if userDesiredRadius >= currentRadius {
				g.sendGeoMessageToFb(userId, geoMessage) // !
			}
		}
	} else {
		logger.Warning(cache.TextCacheDisabled)
	}

	return nil // ok?
}

func (g *GeoMessageUsecase) sendGeoMessageToFb(targetUserId uint64, geoMessage *dd.GeoMessage) {
	if !g.onlineUsersUc.UserIsOnline(targetUserId) {
		return
	}

	count := len(g.feedbackChsForGeoMsgs)
	index := rand.Intn(count)

	g.feedbackChsForGeoMsgs[index] <- input.UserIdWithGeoMessage{
		UserId: targetUserId, GeoMessage: geoMessage}
}
