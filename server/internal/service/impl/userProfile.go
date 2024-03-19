package impl

import (
	"context"
	"encoding/base64"
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
	domainStorage "geoqq/internal/storage/domain"
	dsDto "geoqq/internal/storage/domain/dto"
	fileStorage "geoqq/internal/storage/file"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/file"
	"geoqq/pkg/hash"
	utl "geoqq/pkg/utility"
)

type UserProfileService struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
	hashManager   hash.HashManager
}

func newUserProfileService(deps Dependencies) *UserProfileService {
	instance := &UserProfileService{
		domainStorage: deps.DomainStorage,
		fileStorage:   deps.FileStorage,
		hashManager:   deps.HashManager,
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
		err := p.checkPasswordForUpdate(ctx, userId, input.Security.Password) // hash?
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfile, err)
		}

		// hash hash...

		hashPassword, err := p.hashManager.NewFromString(input.Security.NewPassword)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfile,
				ec.New(err, ec.Server, ec.HashManagerError))
		}

		domainDto.HashPassword = &hashPassword
	}

	if input.Privacy != nil {
		domainDto.Privacy = input.Privacy.ToDynamicDsInp()
	}

	domainDto.Description = input.Description

	// *** save to file and domain storages! ***

	if input.Avatar != nil {
		avatarId, err := p.updateAvatar(ctx, *input.Avatar)
		if err != nil {
			return utl.NewFuncError(p.UpdateUserProfile, err)
		}

		domainDto.AvatarId = &avatarId
	}

	err := p.domainStorage.UpdateUserParts(ctx, userId, domainDto)
	if err != nil {
		return utl.NewFuncError(p.UpdateUserProfile,
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

func (p *UserProfileService) updateAvatar(ctx context.Context, avatar dto.Avatar) (uint64, error) {
	imageExt := file.ImageExt(avatar.Ext)
	if !imageExt.IsValid() {
		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(ErrUnknownImageExtension, ec.Client, ec.UnknownAvatarExtension))
	}
	if len(avatar.Content) == 0 {
		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(ErrImageBodyEmpty, ec.Client, ec.AvatarBodyEmpty)) // check also on delivery layout!
	}

	// ***

	image := file.NewImageForInsert(imageExt, avatar.Content)
	avatarContentBytes, err := base64.StdEncoding.DecodeString(avatar.Content)
	if err != nil {
		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(err, ec.Client, ec.AvatarBodyIsNotBase64)) // or server?
	}

	avatarHash, err := p.hashManager.NewFromBytes(avatarContentBytes)
	if err != nil {
		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(err, ec.Server, ec.HashManagerError))
	}

	// ***

	avatarId, err := p.domainStorage.InsertAvatar(ctx, avatarHash)
	if err != nil {
		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	image.Id = avatarId

	err = p.fileStorage.SaveImage(ctx, image)
	if err != nil {
		_ = p.domainStorage.DeleteAvatarWithId(ctx, avatarId)

		return 0, utl.NewFuncError(p.updateAvatar,
			ec.New(err, ec.Server, ec.FileStorageError))
	}
	return avatarId, nil
}
