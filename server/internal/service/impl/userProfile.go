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

	storageDto, err := p.preparePartUpdateUserPartsInp(ctx, userId, input.PartProfileForUpdate)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
	}

	// ***

	if input.Avatar != nil {
		avatarId, err := p.addImageToUser(ctx,
			input.Avatar.Ext, input.Avatar.Content, userId)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
		}

		storageDto.AvatarId = &avatarId
	}

	// ***

	err = p.domainStorage.UpdateUserParts(ctx, userId, storageDto)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}

func (p *UserProfileService) UpdateUserProfile(ctx context.Context,
	userId uint64, input dto.ProfileForUpdateInp) error {

	storageDto, err := p.preparePartUpdateUserPartsInp(ctx, userId, input.PartProfileForUpdate)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar, err)
	}
	storageDto.AvatarId = input.AvatarId

	// ***

	err = p.domainStorage.UpdateUserParts(ctx, userId, storageDto)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfileWithAvatar,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func (p *UserProfileService) preparePartUpdateUserPartsInp(ctx context.Context,
	userId uint64, input dto.PartProfileForUpdate) (dsDto.UpdateUserPartsInp, error) {

	storageDto := dsDto.UpdateUserPartsInp{}
	if input.Security != nil {
		err := p.checkPasswordForUpdate(ctx, userId, input.Security.PasswordHashInBase64)
		if err != nil {
			return dsDto.UpdateUserPartsInp{},
				utl.NewFuncError(p.preparePartUpdateUserPartsInp, err)
		}

		passwordDoubleHash, err := p.passwordHashInBase64ToPasswordDoubleHash(
			input.Security.NewPasswordHashInBase64)
		if err != nil {
			return dsDto.UpdateUserPartsInp{},
				utl.NewFuncError(p.preparePartUpdateUserPartsInp, err)
		}
		storageDto.PasswordDoubleHash = &passwordDoubleHash // new!
	}

	if input.Privacy != nil {
		storageDto.Privacy = input.Privacy.ToDynamicDsInp()
	}

	storageDto.Description = input.Description // maybe nil
	storageDto.AvatarId = nil

	return storageDto, nil
}

func (p *UserProfileService) checkPasswordForUpdate(ctx context.Context,
	userId uint64, passwordHashInBase64 string) error {

	passwordDoubleHash, err := p.passwordHashInBase64ToPasswordDoubleHash(
		passwordHashInBase64)
	if err != nil {
		return utl.NewFuncError(p.checkPasswordForUpdate, err)
	}

	// ***

	exists, err := p.domainStorage.HasUserByIdAndHashPassword(ctx, userId, passwordDoubleHash)
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
