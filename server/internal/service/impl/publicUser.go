package impl

import (
	"context"
	"geoqq/internal/domain"
	ec "geoqq/internal/pkg/errorForClient/impl"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
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
	userId, targetUserId uint64) (*domain.PublicUser, error) {
	sourceFunc := s.GetPublicUserById

	err := assertUserWithIdExists(ctx,
		s.domainStorage, targetUserId,
		ec.UserNotFound)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	// can also get a deleted user!

	publicUser, err := s.domainStorage.GetPublicUserById(
		ctx, userId, targetUserId)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActivityTimeForUser(userId)
	return publicUser, nil
}

func (s *UserService) GetPublicUserByIds(ctx context.Context,
	userId uint64, targetUserIds []uint64) (domain.PublicUserList, error) {

	if len(targetUserIds) == 0 {
		return domain.PublicUserList{}, nil
	}
	targetUserIds = utl.RemoveDuplicatesFromSlice(targetUserIds)

	// from handler

	exists, err := s.domainStorage.HasUserWithIds(ctx, targetUserIds)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetPublicUserByIds, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return nil, ec.New(ErrOneOrMoreUsersNotFound,
			ec.Client, ec.OneOrMoreUsersNotFound)
	}

	// <---> storage

	publicUsers, err := s.domainStorage.GetPublicUsersByIds(ctx, userId, targetUserIds)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetPublicUserByIds, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActivityTimeForUser(userId)
	return publicUsers, nil
}
