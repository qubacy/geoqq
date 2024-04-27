package impl

import (
	"context"
	"encoding/hex"
	domainStorage "geoqq/internal/storage/domain"
	fileStorage "geoqq/internal/storage/file"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/hash"
	utl "geoqq/pkg/utility"
)

type HasherAndStorages struct {
	fileStorage   fileStorage.Storage
	domainStorage domainStorage.Storage
	hashManager   hash.HashManager
}

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
		return "", utl.NewFuncError(sourceFunc,
			ec.New(err, ec.Server, ec.HashManagerError))
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
		return ec.New(ErrUserNotFound,
			ec.Client, expectedClientCodeForError)
	}
	return nil
}
