package impl

import (
	"context"
	"geoqq_http/internal/domain"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	domainStorage "geoqq_http/internal/storage/domain"
	"geoqq_http/pkg/geoDistance"
	utl "geoqq_http/pkg/utility"
)

type GeoChatMessageService struct {
	domainStorage         domainStorage.Storage
	geoDistanceCalculator geoDistance.Calculator

	generalParams GeneralParams
	chatParams    ChatParams
}

func newGeoChatMessageService(deps Dependencies) *GeoChatMessageService {
	instance := &GeoChatMessageService{
		domainStorage:         deps.DomainStorage,
		geoDistanceCalculator: deps.GeoDistCalculator,
		generalParams:         deps.GeneralParams,
		chatParams:            deps.ChatParams,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *GeoChatMessageService) AddMessageToGeoChat(ctx context.Context,
	userId uint64, text string, longitude, latitude float64) error {

	if len(text) > int(s.chatParams.MaxMessageLength) {
		return ec.New(ErrMessageTooLong(s.chatParams.MaxMessageLength),
			ec.Client, ec.GeoMessageTooLong)
	}

	err := validateLatAndLon(longitude, latitude)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToGeoChat, err)
	}

	// ***

	_, err = s.domainStorage.InsertGeoChatMessageWithUpdateUserLocation(
		ctx, userId, text, latitude, longitude)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToGeoChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	// TODO: send the push message to users?

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	s.domainStorage.UpdateBgrLocationForUser(userId, longitude, latitude)

	return nil
}

// -----------------------------------------------------------------------

func (s *GeoChatMessageService) GetGeoChatAllMessages(
	ctx context.Context, userId uint64,
	distance uint64, latitude, longitude float64) (
	domain.GeoMessageList, error,
) {
	sourceFunc := s.GetGeoChatAllMessages
	err := validateLatAndLon(longitude, latitude)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	err = s.validateDistance(distance)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	var offset uint64 = 0
	var count uint64 = s.chatParams.GeoChatParams.MaxMessageCountReturned

	geoMessages, err := s.domainStorage.GetGeoChatMessages(ctx,
		distance, latitude, longitude,
		offset, count,
	)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// TODO: add limiter requests!

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	s.domainStorage.UpdateBgrLocationForUser(userId, longitude, latitude)

	return geoMessages, nil
}

func (s *GeoChatMessageService) GetGeoChatMessages(
	ctx context.Context, userId uint64,
	distance uint64, latitude, longitude float64,
	offset, count uint64) (
	domain.GeoMessageList, error,
) {
	sourceFunc := s.GetGeoChatMessages
	if count > s.generalParams.MaxPageSize {
		return nil, ec.New(ErrCountMoreThanPermissible,
			ec.Client, ec.CountMoreThanPermissible)
	}

	err := validateLatAndLon(longitude, latitude)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	err = s.validateDistance(distance)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	geoMessages, err := s.domainStorage.GetGeoChatMessages(ctx,
		distance, latitude, longitude,
		offset, count,
	)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	s.domainStorage.UpdateBgrLocationForUser(userId, longitude, latitude)

	return geoMessages, nil
}

// private
// -----------------------------------------------------------------------

func validateLatAndLon(longitude, latitude float64) error {
	/*
		Долгота (longitude) от −180° до +180°
		Широта (latitude) от −90° до +90°
	*/

	// Долгота
	if longitude < -180 || longitude > +180 {
		return ec.New(ErrWrongLongitude, ec.Client, ec.WrongLongitude)
	}

	// Широта
	if latitude < -90 || latitude > +90 {
		return ec.New(ErrWrongLatitude, ec.Client, ec.WrongLatitude)
	}

	return nil
}

func (s *GeoChatMessageService) validateDistance(distance uint64) error {
	if distance > s.chatParams.GeoChatParams.MaxRadius ||
		s.chatParams.GeoChatParams.MinRadius > distance {
		return ec.New(ErrWrongRadius, ec.Client, ec.WrongRadius)
	}
	return nil
}
