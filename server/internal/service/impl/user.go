package impl

import (
	"context"
	"geoqq/internal/domain"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
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
	userId, targetUserId uint64) (domain.PublicUser, error) {

	exists, err := s.domainStorage.HasUserWithId(ctx, targetUserId)
	if err != nil {
		return domain.PublicUser{}, utl.NewFuncError(s.GetPublicUserById,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return domain.PublicUser{}, utl.NewFuncError(s.GetPublicUserById,
			ec.New(ErrUserNotFound, ec.Client, ec.UserNotFound))
	}

	// ***

	publicUser, err := s.domainStorage.GetPublicUserById(ctx, userId, targetUserId)
	if err != nil {
		return domain.PublicUser{}, utl.NewFuncError(s.GetPublicUserById,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return publicUser, nil
}
