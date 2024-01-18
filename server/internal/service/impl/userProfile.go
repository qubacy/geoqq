package impl

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/storage"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type ProfileService struct {
	storage storage.Storage
}

func newProfileService(deps Dependencies) *ProfileService {
	instance := &ProfileService{
		storage: deps.Storage,
	}
	return instance
}

// -----------------------------------------------------------------------

func (p *ProfileService) GetUserProfile(ctx context.Context, userId uint64) (
	domain.UserProfile, error,
) {
	userProfile, err := p.storage.GetUserProfileById(ctx, userId) // should be in storage!
	if err != nil {
		return domain.UserProfile{}, utl.NewFuncError(
			p.GetUserProfile, ec.New(err, ec.Server, ec.StorageError))
	}
	return userProfile, nil
}
