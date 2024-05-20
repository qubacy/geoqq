package impl

import (
	"common/pkg/cache"
	"common/pkg/hash"
	utl "common/pkg/utility"
	"context"
	"encoding/hex"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	domainStorage "geoqq_http/internal/storage/domain"
	fileStorage "geoqq_http/internal/storage/file"
	"regexp"
)

type HasherAndStorages struct {
	enableCache bool
	cache       cache.Cache

	domainStorage domainStorage.Storage
	fileStorage   fileStorage.Storage
	hashManager   hash.HashManager
}

type Validators = map[string]*regexp.Regexp

// -----------------------------------------------------------------------

func passwordHashInHexToPasswordDoubleHash(
	hashManager hash.HashManager, hexValue string) (string, error) {
	sourceFunc := passwordHashInHexToPasswordDoubleHash

	// believe that the module `hex` works correctly!

	passwordHash, err := hex.DecodeString(hexValue)
	if err != nil {
		return "", ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Client, ec.PasswordHashIsNotHex)
	}

	passwordDoubleHash, err := hashManager.NewFromBytes(passwordHash)
	if err != nil {
		return "", ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.HashManagerError)
	}

	return passwordDoubleHash, nil
}

func assertUserWithIdExists(ctx context.Context,
	storage domainStorage.Storage, id uint64,
	expectedClientCodeForError int) error {
	sourceFunc := assertUserWithIdExists

	exists, err := storage.HasUserWithId(ctx, id)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	if !exists {
		return ec.New(ErrUserNotFound, // !
			ec.Client, expectedClientCodeForError)
	}

	return nil
}

func assertUserWithIdNotDeleted(ctx context.Context,
	storage domainStorage.Storage, id uint64,
	expectedClientCodeForError int) error {
	sourceFunc := assertUserWithIdNotDeleted

	wasDeleted, err := storage.WasUserDeleted(ctx, id)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	if wasDeleted {
		return ec.New(ErrUserWithLoginHasBeenDeleted,
			ec.Client, expectedClientCodeForError)
	}

	return nil
}

// check availability only in domain!
func assertImageWithIdExists(ctx context.Context,
	storage domainStorage.Storage, id uint64,
	expectedClientCodeForError int) error {
	sourceFunc := assertImageWithIdExists

	exists, err := storage.HasAvatar(ctx, id)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	if !exists {
		return ec.New(ErrImageNotFound,
			ec.Client, expectedClientCodeForError)
	}
	return nil
}
