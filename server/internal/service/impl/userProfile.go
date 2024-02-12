package impl

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
	domainStorage "geoqq/internal/storage/domain"
	dsDto "geoqq/internal/storage/domain/dto"
	fileStorage "geoqq/internal/storage/file"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type UserProfileService struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
}

func newUserProfileService(deps Dependencies) *UserProfileService {
	instance := &UserProfileService{
		domainStorage: deps.DomainStorage,
		fileStorage:   deps.FileStorage,
	}
	return instance
}

// -----------------------------------------------------------------------

func (p *UserProfileService) GetUserProfile(ctx context.Context, userId uint64) (
	domain.UserProfile, error,
) {
	userProfile, err := p.domainStorage.GetUserProfile(ctx, userId) // should be in storage!
	if err != nil {
		return domain.UserProfile{}, utl.NewFuncError(
			p.GetUserProfile, ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return userProfile, nil
}

func (p *UserProfileService) UpdateUserProfile(ctx context.Context, userId uint64,
	input dto.UpdateProfileInp) error {

	domainDto := dsDto.UpdateUserPartsInp{}
	if input.Security != nil {
		exists, err := p.domainStorage.HasUserByIdAndHashPassword(ctx, userId, input.Security.Password)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfile,
				ec.New(err, ec.Server, ec.DomainStorageError))
		}

		if !exists {
			return utl.NewFuncError(p.UpdateUserProfile,
				ec.New(ErrIncorrectPassword, ec.Client, ec.IncorrectPassword))
		}

		domainDto.HashPassword = &input.Security.NewPassword
	}
	domainDto.Description = input.Description

	if input.Privacy != nil {
		domainDto.Privacy = input.Privacy.ToDsInp()
	}

	// ***

	if input.Avatar != nil {

	}

	// TODO:

	return nil
}
