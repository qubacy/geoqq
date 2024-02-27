package impl

import (
	"context"
	"geoqq/internal/domain"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
)

type UserService struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
}

func newUserService(deps Dependencies) *UserService {
	instance := &UserService{
		fileStorage:   deps.FileStorage,
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *UserService) GetPublicUserById(ctx context.Context,
	srcUserId, targetUserId uint64) (domain.PublicUser, error) {

}
