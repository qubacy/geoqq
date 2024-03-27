package impl

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
	dsDto "geoqq/internal/storage/domain/dto"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type UserProfileService struct {
	HasherAndStorages
}

func newUserProfileService(deps Dependencies) *UserProfileService {
	instance := &UserProfileService{
		HasherAndStorages{
			domainStorage: deps.DomainStorage,
			fileStorage:   deps.FileStorage,
			hashManager:   deps.HashManager,
		},
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

func (p *UserProfileService) UpdateUserProfileWithAvatar(ctx context.Context, userId uint64,
	input dto.ProfileWithAvatarForUpdateInp) error {

	domainDto := dsDto.UpdateUserPartsInp{}
	if input.Security != nil {
		err := p.checkPasswordForUpdate(ctx, userId, input.Security.Password) // hash?
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
		}

		// hash hash...

		hashPassword, err := p.hashManager.NewFromString(input.Security.NewPassword)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
				ec.New(err, ec.Server, ec.HashManagerError))
		}

		domainDto.HashPassword = &hashPassword
	}

	if input.Privacy != nil {
		domainDto.Privacy = input.Privacy.ToDynamicDsInp()
	}

	domainDto.Description = input.Description // maybe nil

	// *** save to file and domain storages! ***

	if input.Avatar != nil {
		avatarId, err := p.addImageToUser(ctx,
			input.Avatar.Ext, input.Avatar.Content, userId)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
		}

		domainDto.AvatarId = &avatarId
	}

	err := p.domainStorage.UpdateUserParts(ctx, userId, domainDto)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}

func (p *UserProfileService) UpdateUserProfile(ctx context.Context,
	userId uint64, input dto.ProfileForUpdateInp) error {
	domainDto := dsDto.UpdateUserPartsInp{}
	if input.Security != nil {
		err := p.checkPasswordForUpdate(ctx, userId, input.Security.Password) // hash?
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
		}

		// hash hash...

		hashPassword, err := p.hashManager.NewFromString(input.Security.NewPassword)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
				ec.New(err, ec.Server, ec.HashManagerError))
		}

		domainDto.HashPassword = &hashPassword
	}

	if input.Privacy != nil {
		domainDto.Privacy = input.Privacy.ToDynamicDsInp()
	}

	domainDto.Description = input.Description // maybe nil
	domainDto.AvatarId = input.AvatarId

	err := p.domainStorage.UpdateUserParts(ctx, userId, domainDto)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func (p *UserProfileService) checkPasswordForUpdate(ctx context.Context,
	userId uint64, password string) error {
	hashPassword, err := p.hashManager.NewFromString(password)
	if err != nil {
		return utl.NewFuncError(p.checkPasswordForUpdate,
			ec.New(err, ec.Server, ec.HashManagerError))
	}

	exists, err := p.domainStorage.HasUserByIdAndHashPassword(ctx, userId, hashPassword)
	if err != nil {
		return utl.NewFuncError(p.checkPasswordForUpdate,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	if !exists {
		return utl.NewFuncError(p.checkPasswordForUpdate,
			ec.New(ErrIncorrectPassword, ec.Client, ec.IncorrectPasswordWhenUpdate))
	}

	return nil
}
