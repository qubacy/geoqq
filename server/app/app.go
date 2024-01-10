package app

import (
	"context"
	"geoqq/internal/config"
	deliveryHttp "geoqq/internal/delivery/http"
	"geoqq/internal/server"
	"geoqq/internal/service"
	serviceImpl "geoqq/internal/service/impl"
	"geoqq/internal/storage"
	storagePostgre "geoqq/internal/storage/impl/sql/postgre"
	"geoqq/pkg/hash"
	hashImpl "geoqq/pkg/hash/impl"
	"geoqq/pkg/token"
	tokenImpl "geoqq/pkg/token/impl"
	"geoqq/pkg/utility"

	"github.com/spf13/viper"
)

type App struct {
	server *server.Server
	//...
}

func NewApp() (*App, error) {
	err := config.Initialize()
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
	}

	// *** common deps!

	tokenManager, err := tokenManagerInstance()
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
	}
	hashManager, err := hashManagerInstance()
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
	}

	// *** storage

	storage, err := storageInstance()
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
	}

	// *** service

	services, err := servicesInstance(
		tokenManager, hashManager, storage)
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
	}

	// *** delivery with http

	deliveryHttpDeps := deliveryHttp.Dependencies{
		TokenExtractor: tokenManager,
		Services:       services,
	}
	httpHandler, err := deliveryHttp.NewHandler(deliveryHttpDeps)
	if err != nil {
		return nil, utility.CreateCustomError(NewApp, err)
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

func storageInstance() (storage.Storage, error) {
	maxInitTime := viper.GetDuration("storage.max_init_time")
	ctx, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer cancel()

	var err error = ErrStorageTypeIsNotDefined
	var storage storage.Storage = nil

	storageType := viper.GetString("storage.type")
	if storageType == "postgre" {
		storage, err = storagePostgre.NewStorage(ctx, storagePostgre.Dependencies{
			Host:     viper.GetString("storage.sql.postgre.host"),
			Port:     viper.GetUint16("storage.sql.postgre.port"),
			User:     viper.GetString("storage.sql.postgre.user"),
			Password: viper.GetString("storage.sql.postgre.password"),
			DbName:   viper.GetString("storage.sql.postgre.database"),
		})
	} else if storageType == "memory" {
		//...
	} else if storageType == "sqlite" {
		//...
	}

	if err != nil {
		return nil,
			utility.CreateCustomError(storageInstance, err) // <--- exception!
	}
	return storage, nil
}

func servicesInstance(
	tokenManager token.TokenManager,
	hashManager hash.HashManager,
	storage storage.Storage) (
	service.Services, error,
) {
	services, err := serviceImpl.NewServices(serviceImpl.Dependencies{
		TokenManager:    tokenManager,
		AccessTokenTTL:  viper.GetDuration("delivery.token.access_ttl"),
		RefreshTokenTTL: viper.GetDuration("delivery.token.refresh_ttl"),
		HashManager:     hashManager,
		Storage:         storage,
	})
	if err != nil {
		return nil, utility.CreateCustomError(servicesInstance, err)
	}

	return services, err
}

// -----------------------------------------------------------------------

func hashManagerInstance() (hash.HashManager, error) {
	hashType, err := hashImpl.StrToHashType(
		viper.GetString("storage.hash_type"))

	if err != nil {
		return nil, utility.CreateCustomError(hashManagerInstance, err)
	}

	hashManager, err := hashImpl.NewHashManager(hashType)
	if err != nil {
		return nil, utility.CreateCustomError(hashManagerInstance, err)
	}

	return hashManager, nil
}

func tokenManagerInstance() (token.TokenManager, error) {
	signingKey := viper.GetString("delivery.token.signing_key")
	tokenManager, err := tokenImpl.NewTokenManager(signingKey)
	if err != nil {
		return nil, utility.CreateCustomError(tokenManagerInstance, err)
	}

	return tokenManager, nil
}
