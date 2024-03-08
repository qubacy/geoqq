package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
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

	return nil
}
