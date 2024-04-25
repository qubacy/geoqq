package app

import (
	"context"
	"errors"
	"fmt"
	"geoqq/app/firstStart"
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
	geoDistanceImpl "geoqq/pkg/geoDistance/impl/haversine"
	"geoqq/pkg/hash"
	hashImpl "geoqq/pkg/hash/impl"
	"geoqq/pkg/logger"
	"geoqq/pkg/logger/impl"
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

// or ctx for cancel background work!
func NewApp(ctxWithCancel context.Context) (*App, error) {
	err := config.Initialize()
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	// *** logger

	err = initializeLogging()
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

	domainStorage, err := domainStorageInstance(ctxWithCancel)
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}
	fileStorage, err := fileStorageInstance(hashManager)
	if err != nil {
		return nil, utility.NewFuncError(NewApp, err)
	}

	maxInitTime := viper.GetDuration("first_start.max_init_time") // ?
	ctxForInit, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer func() { cancel() }()

	err = firstStart.InsertDataIntoStorages(
		ctxForInit, domainStorage, fileStorage,
		hashManager,
	)
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

	// *** start server!

	serverDeps := server.Dependencies{
		Engine: httpHandler.GetEngine(),

		Host: viper.GetString("server.http.host"),
		Port: viper.GetUint16("server.http.port"),

		ReadTimeout:  viper.GetDuration("server.http.read_timeout"),
		WriteTimeout: viper.GetDuration("server.http.write_timeout"),
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

func (a *App) Stop() error {
	return errors.Join(
		a.server.Stop(),
		logger.Close(),
	)
}

// logging
// -----------------------------------------------------------------------

func initializeLogging() error {
	loggingType := viper.GetString("logging.type")
	if loggingType == "lumberjack" {
		logger.Initialize(impl.SetLumberjackLoggerForStdOutput(
			logger.Level(viper.GetInt("logging.level")),
			viper.GetBool("logging.use_console"),

			viper.GetString("logging.lumberjack.dirname"),
			viper.GetString("logging.lumberjack.filename"),

			viper.GetInt("logging.lumberjack.max_size_mb"),
			viper.GetInt("logging.lumberjack.max_backups"),
			viper.GetInt("logging.lumberjack.max_age_days"),
		))
		return nil

	} else if loggingType == "mlog" {
		//...
		return ErrNotImplemented
	}

	return ErrLoggingTypeIsNotDefined
}

// storages
// -----------------------------------------------------------------------

func domainStorageInstance(ctxWithCancel context.Context) (domainStorage.Storage, error) {
	maxInitTime := viper.GetDuration("storage.max_init_time")
	logger.Trace("storage.max_init_time: %v", maxInitTime)

	ctxForInit, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer func() {
		fmt.Println("CtxForInit will be canceled")
		cancel()
	}()

	var err error = ErrDomainStorageTypeIsNotDefined
	var storage domainStorage.Storage = nil

	storageType := viper.GetString("storage.domain.type")
	if storageType == "postgre" {
		storage, err = domainStorageImpl.NewStorage(
			ctxForInit, ctxWithCancel,
			domainStorageImpl.Dependencies{
				Host:     viper.GetString("storage.domain.sql.postgre.host"),
				Port:     viper.GetUint16("storage.domain.sql.postgre.port"),
				User:     viper.GetString("storage.domain.sql.postgre.user"),
				Password: viper.GetString("storage.domain.sql.postgre.password"),
				DbName:   viper.GetString("storage.domain.sql.postgre.database"),

				DependenciesForBgr: domainStorageImpl.DependenciesForBgr{
					MaxQueryCount: viper.GetInt("storage.domain.sql.postgre.background.max_query_count"),
					QueryTimeout:  viper.GetDuration("storage.domain.sql.postgre.background.query_timeout"),
				},
			})
	} else if storageType == "sqlite" {
		//...
		return nil, ErrNotImplemented
	}

	if err != nil {
		return nil,
			utility.NewFuncError(domainStorageInstance, err) // may be edge!
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
		return nil, ErrNotImplemented
	}

	if err != nil {
		return nil,
			utility.NewFuncError(fileStorageInstance, err)
	}

	return storage, nil
}

// services
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
		HashManager:  hashManager,
		TokenManager: tokenManager,

		AccessTokenTTL:  viper.GetDuration("delivery.token.access_ttl"),
		RefreshTokenTTL: viper.GetDuration("delivery.token.refresh_ttl"),

		DomainStorage:   domainStorage,
		FileStorage:     fileStorage,
		AvatarGenerator: avatarGenerator,

		GeoDistCalculator: geoDistanceImpl.NewCalculator(),

		GeneralParams: serviceImpl.GeneralParams{
			MaxPageSize: viper.GetUint64("service.general.pagination.max_page_size"),
		},

		AuthParams: serviceImpl.AuthParams{
			SignIn: serviceImpl.SignInParams{
				FailedAttemptCount: viper.GetUint32("service.auth.sign_in.failed_attempt_count"),
				FailedAttemptTtl:   viper.GetDuration("service.auth.sign_in.failed_attempt_ttl"),
				BlockingTime:       viper.GetDuration("service.auth.sign_in.blocking_time"),
			},
			SignUp: serviceImpl.SignUpParams{
				BlockingTime: viper.GetDuration("service.auth.sign_up.blocking_time"),
			},
		},
	})
	if err != nil {
		return nil, utility.NewFuncError(servicesInstance, err)
	}

	return services, err
}

// common
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
