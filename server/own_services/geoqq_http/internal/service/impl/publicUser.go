package impl

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	"crypto/md5"
	"fmt"
	"geoqq_http/internal/domain"
	domainStorage "geoqq_http/internal/storage/domain"
	fileStorage "geoqq_http/internal/storage/file"
	"strconv"
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
		ec.UserNotFound,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	// can also get a deleted user!

	publicUser, err := s.domainStorage.GetTransformedPublicUserById(ctx,
		userId, targetUserId, transformUsernameToDeleted)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
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

	publicUsers, err := s.domainStorage.GetTransformedPublicUsersByIds(ctx,
		userId, targetUserIds, transformUsernameToDeleted)
	if err != nil {
		return nil, ec.New(utl.NewFuncError(s.GetPublicUserByIds, err),
			ec.Server, ec.DomainStorageError)
	}

	s.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return publicUsers, nil
}

// transform
// -----------------------------------------------------------------------

var usernamesForDeleted = []string{
	"disappeared",
	"erased",
	"echo in the void",
	"abandoned",
	"extinct",
	"missing",
	//...
}

func transformUsernameToDeleted(pu *domain.PublicUser) { // not nil!
	if !pu.IsDeleted {
		return
	}

	h := md5.Sum([]byte(strconv.FormatUint(pu.Id, 10)))
	index := 0
	for i := range h {
		index += int(h[i])
	}
	index = index % len(usernamesForDeleted)
	pu.Username = fmt.Sprintf("<%v>",
		usernamesForDeleted[index])
}
