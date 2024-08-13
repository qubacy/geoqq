package usecase

import (
	domain "common/pkg/domain/geoqq"
	ec "common/pkg/errorForClient/geoqq"
	geo "common/pkg/geoDistance"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/cache"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
	"math/rand"
	"reflect"
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

// From `geoqq http`!

func (g *GeoMessageUsecase) ForwardGeoMessage(ctx context.Context,
	gm *domain.GeoMessage, lon, lat float64) error {
	/*
		Geo message has already
			been added to the database!
	*/

	if gm == nil {
		typeName := reflect.TypeOf(gm)
		return constErrors.ErrInputParamWithTypeNotSpecified(
			typeName.Name())
	}

	if err := g.sendGeoMessageToWhoeverNeeds(ctx, input.EventAdded, // !
		gm, lon, lat); err != nil {
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
		geoMessage   *domain.GeoMessage
	)

	if geoMessageId, err = g.db.InsertGeoMessage(ctx, userId, text, lon, lat); err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err), ec.Server, ec.DomainStorageError)
	}
	geoMessage = domain.NewGeoMessage(geoMessageId, userId, text)

	// ***

	if err = g.sendGeoMessageToWhoeverNeeds(ctx, input.EventAdded, // !
		geoMessage, lon, lat); err != nil {
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))

		return nil // ?
	}

	// ***

	g.db.UpdateBgrLocationForUser(userId, lon, lat)
	g.db.UpdateBgrLastActionTimeForUser(userId)

	return nil // ok
}

func (g *GeoMessageUsecase) GetFbChansForGeoMessages() []<-chan input.UserIdWithGeoMessage {
	return utl.ChanToLeftDirected(g.feedbackChsForGeoMsgs)
}

// private
// -----------------------------------------------------------------------

func (g *GeoMessageUsecase) sendGeoMessageToWhoeverNeeds(ctx context.Context, event input.Event,
	geoMessage *domain.GeoMessage, lon, lat float64) error {
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

			if userDesiredRadius >= currentRadius { // important!
				ue := input.MakeUserIdWithEvent(userId, event)
				g.sendGeoMessageToFb(ue, geoMessage)
			}
		}
	} else {
		// TODO: then use persistence!

		logger.Warning(cache.TextCacheDisabled)
	}

	return nil // ok?
}

func (g *GeoMessageUsecase) sendGeoMessageToFb(
	ue input.UserIdWithEvent, geoMessage *domain.GeoMessage) {

	targetUserId := ue.GetUserId()
	if !g.onlineUsersUc.UserIsOnline(targetUserId) {
		logger.Debug("user with id `%v` is offline", targetUserId)
		return
	}

	count := len(g.feedbackChsForGeoMsgs)
	index := rand.Intn(count)

	g.feedbackChsForGeoMsgs[index] <- input.UserIdWithGeoMessage{
		UserIdWithEvent: ue, GeoMessage: geoMessage}
}
