package impl

import (
	"common/pkg/cache"
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/infra/msgs"
	"geoqq_http/internal/service/dto"
	dsDto "geoqq_http/internal/storage/domain/dto"
	"time"
)

type UserProfileService struct {
	HasherAndStorages
	accessTokenTTL time.Duration
	userParams     UserParams
	validators     Validators

	msgs msgs.Msgs
}

func newUserProfileService(deps Dependencies) (*UserProfileService, error) {
	instance := &UserProfileService{
		HasherAndStorages: HasherAndStorages{
			enableCache: deps.EnableCache,
			cache:       deps.Cache,

			domainStorage: deps.DomainStorage,
			fileStorage:   deps.FileStorage,
			hashManager:   deps.HashManager,
		},
		accessTokenTTL: deps.AccessTokenTTL,
		userParams:     deps.UserParams,

		msgs: deps.Msgs,
	}

	// ***

	if err := instance.initializeValidators(); err != nil {
		return nil, utl.NewFuncError(newUserProfileService, err)
	}
	return instance, nil
}

// -----------------------------------------------------------------------

func (p *UserProfileService) GetUserProfile(ctx context.Context, userId uint64) (
	*domain.UserProfile, error,
) {
	userProfile, err := p.domainStorage.GetUserProfile(ctx, userId) // should be in storage!
	if err != nil {
		return nil, ec.New(utl.NewFuncError(p.GetUserProfile, err),
			ec.Server, ec.DomainStorageError)
	}

	p.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	return userProfile, nil
}

func (p *UserProfileService) UpdateUserProfileWithAvatar(ctx context.Context, userId uint64,
	input dto.ProfileWithAvatarForUpdateInp) error {
	sourceFunc := p.UpdateUserProfileWithAvatar

	if input.Avatar != nil {
		if err := p.assertAddImageNotBlockedForUser(ctx, userId); err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}
	}

	if input.Username != nil {
		if err := p.assertChangeUsernameNotBlockedForUser(ctx, userId); err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if err := p.validateUsername(*input.Username); err != nil {
			return ec.New(utl.NewFuncError(sourceFunc, err),
				ec.Client, ec.ValidateUsernameFailed)
		}
	}

	// preparation before updating (but the image is added to storages!)

	storageDto, err := p.preparePartUpdateUserPartsInp(ctx, userId, input.PartProfileForUpdate)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	if input.Avatar != nil {
		avatarId, err := p.addImageToUser(ctx,
			input.Avatar.Ext, input.Avatar.Content, userId)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		storageDto.AvatarId = &avatarId
	}

	// update

	p.actionsAfterUpdateUserProfile(ctx, userId, storageDto)

	return nil
}

func (p *UserProfileService) UpdateUserProfile(ctx context.Context,
	userId uint64, input dto.ProfileForUpdateInp) error {
	sourceFunc := p.UpdateUserProfile

	if input.Username != nil {
		if err := p.assertChangeUsernameNotBlockedForUser(ctx, userId); err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if err := p.validateUsername(*input.Username); err != nil {
			return ec.New(utl.NewFuncError(sourceFunc, err),
				ec.Client, ec.ValidateUsernameFailed)
		}
	}

	if input.AvatarId != nil {
		err := assertImageWithIdExists(ctx,
			p.domainStorage, *input.AvatarId,
			ec.ImageNotFoundWhenUpdate,
		)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}
	}

	// preparation before updating

	storageDto, err := p.preparePartUpdateUserPartsInp(ctx,
		userId, input.PartProfileForUpdate)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	storageDto.AvatarId = input.AvatarId

	// update

	if err = p.domainStorage.UpdateUserParts(ctx, userId, storageDto); err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err), ec.Server, ec.DomainStorageError)
	}

	p.actionsAfterUpdateUserProfile(ctx, userId, storageDto) // quiet!

	return nil
}

func (p *UserProfileService) actionsAfterUpdateUserProfile(
	ctx context.Context, userId uint64, storageDto *dsDto.UpdateUserPartsInp) {
	sourceFunc := p.actionsAfterUpdateUserProfile

	p.executeUpdateChangeUsernameCacheIfNeeded(ctx, userId, storageDto.Username != nil)
	p.domainStorage.UpdateBgrLastActionTimeForUser(userId)

	if p.msgs != nil && storageDto.HasFieldsNotNilIgnorePassword() {
		if err := p.msgs.SendPublicUserId(ctx, msgs.EventUpdatedPublicUser, userId); err != nil {
			logger.Warning("%v", utl.NewFuncError(sourceFunc, err))
		}
	} else {
		logger.Warning(msgs.TextMsgsDisabled)
	}
}

// -----------------------------------------------------------------------

func (p *UserProfileService) DeleteUserProfile(ctx context.Context, userId uint64) error {
	sourceFunc := p.DeleteUserProfile
	err := p.domainStorage.DeleteUserProfile(ctx, userId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	if p.enableCache {
		if err = p.updateDeletedUserCache(ctx, userId); err != nil {
			logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		} else {
			logger.Info("user %v added to cache as deleted", userId)
		}

	} else {
		logger.Warning(cache.TextCacheDisabled)
	}

	// update

	p.domainStorage.UpdateBgrLastActionTimeForUser(userId)
	p.domainStorage.DeleteBgrMateChatsForUser(userId) // !

	if p.msgs != nil {
		if err := p.msgs.SendPublicUserId(ctx, msgs.EventUpdatedPublicUser, userId); err != nil {
			logger.Warning("%v", utl.NewFuncError(sourceFunc, err))
		}
	} else {
		logger.Warning(msgs.TextMsgsDisabled)
	}

	return nil
}

// private
// -----------------------------------------------------------------------

func (p *UserProfileService) preparePartUpdateUserPartsInp(ctx context.Context,
	userId uint64, input dto.PartProfileForUpdate) (*dsDto.UpdateUserPartsInp, error) {
	sourceFunc := p.preparePartUpdateUserPartsInp

	storageDto := dsDto.UpdateUserPartsInp{}
	if input.Security != nil {
		err := p.checkPasswordForUpdate(ctx, userId, input.Security.PasswordHash)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		passwordDoubleHash, err := passwordHashInHexToPasswordDoubleHash(
			p.hashManager, input.Security.NewPasswordHash)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}
		storageDto.PasswordDoubleHash = &passwordDoubleHash // new!
	}

	if input.Privacy != nil {
		storageDto.Privacy = input.Privacy.ToDynamicDsInp()
	}

	storageDto.Username = input.Username
	storageDto.Description = input.Description // maybe nil
	storageDto.AvatarId = nil                  // !

	return &storageDto, nil
}

func (p *UserProfileService) checkPasswordForUpdate(ctx context.Context,
	userId uint64, passwordHash string) error {

	passwordDoubleHash, err :=
		passwordHashInHexToPasswordDoubleHash(p.hashManager, passwordHash)

	if err != nil {

		// Contains general client code for user!
		return utl.NewFuncError(p.checkPasswordForUpdate, err)
	}

	// ***

	exists, err := p.domainStorage.HasUserByIdAndHashPassword(ctx, userId, passwordDoubleHash)
	if err != nil {
		return ec.New(utl.NewFuncError(p.checkPasswordForUpdate, err),
			ec.Server, ec.DomainStorageError)
	}

	if !exists {
		return ec.New(ErrIncorrectPassword,
			ec.Client, ec.IncorrectPasswordWhenUpdate)
	}

	return nil
}

// -----------------------------------------------------------------------

func (p *UserProfileService) executeUpdateChangeUsernameCacheIfNeeded(
	ctx context.Context, userId uint64, usernameWasChanged bool) {
	if p.cache == nil {
		logger.Warning(cache.TextCacheDisabled)
		return
	}

	if usernameWasChanged {
		if err := p.updateChangeUsernameCache(ctx, userId); err != nil {
			err = utl.NewFuncError(p.executeUpdateChangeUsernameCacheIfNeeded, err)
			logger.Error("%v", err)

		} else {
			logger.Info("change username cache updated for user %v", userId)
		}
	}
}
