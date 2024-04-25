package impl

import (
	"context"
	"geoqq/internal/domain"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/geoDistance"
	utl "geoqq/pkg/utility"
)

type GeoChatMessageService struct {
	domainStorage         domainStorage.Storage
	geoDistanceCalculator geoDistance.Calculator
	generalParams         GeneralParams
}

func newGeoChatMessageService(deps Dependencies) *GeoChatMessageService {
	instance := &GeoChatMessageService{
		domainStorage:         deps.DomainStorage,
		geoDistanceCalculator: deps.GeoDistCalculator,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *GeoChatMessageService) AddMessageToGeoChat(ctx context.Context,
	userId uint64, text string,
	longitude, latitude float64) error {

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

	return nil
}

// -----------------------------------------------------------------------

func (s *GeoChatMessageService) GetGeoChatAllMessages(
	ctx context.Context, distance uint64,
	latitude, longitude float64) (
	domain.GeoMessageList, error,
) {
	err := validateLatAndLon(longitude, latitude)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatAllMessages, err)
	}

	// ***

	geoMessages, err := s.domainStorage.GetGeoChatAllMessages(
		ctx, distance, latitude, longitude)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatAllMessages,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return geoMessages, nil
}

func (s *GeoChatMessageService) GetGeoChatMessages(
	ctx context.Context, distance uint64,
	latitude, longitude float64,
	offset, count uint64) (
	domain.GeoMessageList, error,
) {
	if count > s.generalParams.MaxPageSize {
		return nil, ec.New(ErrCountMoreThanPermissible,
			ec.Client, ec.CountMoreThanPermissible)
	}

	err := validateLatAndLon(longitude, latitude)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatMessages, err)
	}

	// ***

	geoMessages, err := s.domainStorage.GetGeoChatMessages(
		ctx, distance, latitude, longitude, offset, count)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatMessages,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

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
		return utl.NewFuncError(validateLatAndLon,
			ec.New(ErrWrongLongitude, ec.Client, ec.WrongLongitude))
	}

	// Широта
	if latitude < -90 || latitude > +90 {
		return utl.NewFuncError(validateLatAndLon,
			ec.New(ErrWrongLatitude, ec.Client, ec.WrongLatitude))
	}

	return nil
}
