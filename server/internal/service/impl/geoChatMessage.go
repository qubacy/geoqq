package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type GeoChatMessageService struct {
	domainStorage domainStorage.Storage
}

func newGeoChatMessageService(deps Dependencies) *GeoChatMessageService {
	instance := &GeoChatMessageService{
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *GeoChatMessageService) AddMessageToGeoChat(ctx context.Context,
	userId uint64, text string,
	longitude, latitude float64) error {

	/*
		Долгота (longitude) от −180° до +180°
		Широта (latitude) от −90° до +90°
	*/

	// Долгота
	if longitude < -180 || longitude > +180 {
		return utl.NewFuncError(s.AddMessageToGeoChat,
			ec.New(ErrWrongLongitude, ec.Client, ec.WrongLongitude))
	}
	// Широта
	if latitude < -90 || latitude > +90 {
		return utl.NewFuncError(s.AddMessageToGeoChat,
			ec.New(ErrWrongLatitude, ec.Client, ec.WrongLatitude))
	}

	// ***

	_, err := s.domainStorage.InsertGeoChatMessage(ctx, userId, text,
		latitude, longitude)
	if err != nil {
		return utl.NewFuncError(s.AddMessageToGeoChat,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	// TODO: send the push message to users!

	return nil
}
