package app

import (
	"context"
	"geoqq/internal/config"
	deliveryHttp "geoqq/internal/delivery/http"
	"geoqq/internal/server"
	"geoqq/internal/service"
	serviceImpl "geoqq/internal/service/impl"
	domainStorage "geoqq/internal/storage/domain"
	domainStorageImpl "geoqq/internal/storage/domain/impl/sql/postgre"
	fileStorage "geoqq/internal/storage/file"
	fileStorageImpl "geoqq/internal/storage/file/impl/memory/anytime"
	"geoqq/pkg/avatar"
	avatarImpl "geoqq/pkg/avatar/impl"
	"geoqq/pkg/hash"
	hashImpl "geoqq/pkg/hash/impl"
	"geoqq/pkg/token"
	tokenImpl "geoqq/pkg/token/impl"
	"geoqq/pkg/utility"
	"strings"

	"github.com/spf13/viper"
)

type App struct {
	server *server.Server
	//...
}

func NewApp() (*App, error) {
	err := config.Initialize()
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	// *** common deps!

	tokenManager, err := tokenManagerInstance()
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}
	hashManager, err := hashManagerInstance()
	if err != nil {
		return nil, utility.NewFuncError(servicesInstance, err)
	}

	// *** storage

	domainStorage, err := domainStorageInstance()
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}
	fileStorage, err := fileStorageInstance(hashManager)
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	// *** service

	services, err := servicesInstance(
		tokenManager, hashManager,
		domainStorage, fileStorage,
	)
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	// *** delivery with http

	httpHandler, err := deliveryHttp.NewHandler(deliveryHttp.Dependencies{
		TokenExtractor: tokenManager,
		Services:       services,
	})
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	// *** server

	serverDeps := server.Dependencies{
		Engine: httpHandler.GetEngine(),
	}

	server, err := server.NewServer(serverDeps)
	if err != nil {
		return nil, err
	}

	return &App{
		server: server,
	}, nil
}

func (a *App) Run() error {
	return a.server.Start()
}

// private
// -----------------------------------------------------------------------

func domainStorageInstance() (domainStorage.Storage, error) {
	maxInitTime := viper.GetDuration("storage.max_init_time")
	ctx, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer cancel()

	var err error = ErrDomainStorageTypeIsNotDefined
	var storage domainStorage.Storage = nil

	storageType := viper.GetString("storage.domain.type")
	if storageType == "postgre" {
		storage, err = domainStorageImpl.NewStorage(ctx, domainStorageImpl.Dependencies{
			Host:     viper.GetString("storage.domain.sql.postgre.host"),
			Port:     viper.GetUint16("storage.domain.sql.postgre.port"),
			User:     viper.GetString("storage.domain.sql.postgre.user"),
			Password: viper.GetString("storage.domain.sql.postgre.password"),
			DbName:   viper.GetString("storage.domain.sql.postgre.database"),
		})
	} else if storageType == "sqlite" {
		//...
	}

	if err != nil {
		return nil,
			utility.NewFuncError(domainStorageInstance, err) // <--- exception!
	}
	return storage, nil
}

func fileStorageInstance(hashManager hash.HashManager) (fileStorage.Storage, error) {
	var err error = ErrFileStorageTypeIsNotDefined
	var storage fileStorage.Storage = nil

	storageType := viper.GetString("storage.file.type")
	if storageType == "anytime" {
		storage, err = fileStorageImpl.NewStorage(fileStorageImpl.Dependencies{
			AvatarDirName: strings.Join([]string{
				viper.GetString("storage.file.memory.anytime.root"),
				viper.GetString("storage.file.memory.anytime.avatar"),
			}, "/"),
			HashManager: hashManager,
		})
	} else if storageType == "runtime" {
		//...
	}

	if err != nil {
		return nil,
			utility.NewFuncError(fileStorageInstance, err)
	}

	return storage, nil
}

// -----------------------------------------------------------------------

func servicesInstance(
	tokenManager token.TokenManager,
	hashManager hash.HashManager,
	domainStorage domainStorage.Storage,
	fileStorage fileStorage.Storage) (
	service.Services, error,
) {

	// deps only for services

	avatarGenerator, err := avatarGeneratorInstance()
	if err != nil {
		return nil, utility.NewFuncError(servicesInstance, err)
	}

	// ***

	services, err := serviceImpl.NewServices(serviceImpl.Dependencies{
		HashManager:     hashManager,
		TokenManager:    tokenManager,
		AccessTokenTTL:  viper.GetDuration("delivery.token.access_ttl"),
		RefreshTokenTTL: viper.GetDuration("delivery.token.refresh_ttl"),
		AvatarGenerator: avatarGenerator,
		DomainStorage:   domainStorage,
		FileStorage:     fileStorage,
	})
	if err != nil {
		return nil, utility.NewFuncError(servicesInstance, err)
	}

	return services, err
}

// -----------------------------------------------------------------------

func hashManagerInstance() (hash.HashManager, error) {
	hashType, err := hashImpl.StrToHashType(
		viper.GetString("storage.hash_type"))

	if err != nil {
		return nil, utility.NewFuncError(hashManagerInstance, err)
	}

	hashManager, err := hashImpl.NewHashManager(hashType)
	if err != nil {
		return nil, utility.NewFuncError(hashManagerInstance, err)
	}

	return hashManager, nil
}

func tokenManagerInstance() (token.TokenManager, error) {
	signingKey := viper.GetString("delivery.token.signing_key")
	tokenManager, err := tokenImpl.NewTokenManager(signingKey)
	if err != nil {
		return nil, utility.NewFuncError(tokenManagerInstance, err)
	}

	return tokenManager, nil
}

func avatarGeneratorInstance() (avatar.AvatarGenerator, error) {
	return avatarImpl.NewAvatarGenerator()
}
