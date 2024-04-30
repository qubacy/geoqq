package app

import (
	"context"
	"errors"
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
	"geoqq/pkg/cache"
	redisCache "geoqq/pkg/cache/redisCache"
	geoDistanceImpl "geoqq/pkg/geoDistance/impl/haversine"
	"geoqq/pkg/hash"
	hashImpl "geoqq/pkg/hash/impl"
	"geoqq/pkg/logger"
	"geoqq/pkg/logger/impl"
	"geoqq/pkg/token"
	tokenImpl "geoqq/pkg/token/impl"
	utl "geoqq/pkg/utility"
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
		return nil, utl.NewFuncError(NewApp, err)
	}

	// *** logger

	err = initializeLogging()
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}

	// *** common deps!

	tokenManager, err := tokenManagerInstance()
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}
	hashManager, err := hashManagerInstance()
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}

	var cacheInst cache.Cache = nil
	enableCache := viper.GetBool("cache.enable")
	if enableCache {
		cacheInst, err = createCacheInstance()
		if err != nil {
			return nil, utl.NewFuncError(NewApp, err)
		}
	}

	// *** storage

	domainStorage, err := domainStorageInstance(ctxWithCancel)
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}
	fileStorage, err := fileStorageInstance(hashManager)
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}

	maxInitTime := viper.GetDuration("first_start.max_init_time") // ?
	ctxForInit, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer func() { cancel() }()

	err = firstStart.InsertDataIntoStorages(
		ctxForInit, domainStorage, fileStorage,
		hashManager,
	)
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}

	// *** service

	services, err := servicesInstance(
		tokenManager, hashManager,
		cacheInst, domainStorage, fileStorage,
	)
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}

	// *** delivery with http

	httpHandler, err := deliveryHttp.NewHandler(deliveryHttp.Dependencies{
		TokenExtractor: tokenManager,
		Services:       services,
	})
	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
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
	} else if loggingType == "mlog" {
		//...
		return ErrNotImplemented
	} else {
		return ErrLoggingTypeIsNotDefined
	}

	return nil
}

// storages
// -----------------------------------------------------------------------

func domainStorageInstance(ctxWithCancel context.Context) (domainStorage.Storage, error) {
	storageType := viper.GetString("storage.domain.type")
	logger.Info("storage type: %v", storageType)

	maxInitTime := viper.GetDuration("storage.max_init_time")
	ctxForInit, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer func() { cancel() }()

	var err error
	var storage domainStorage.Storage
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
	} else {
		return nil, ErrDomainStorageTypeIsNotDefined
	}

	if err != nil {
		return nil, utl.NewFuncError(domainStorageInstance, err) // may be edge!
	}
	return storage, nil
}

func fileStorageInstance(hashManager hash.HashManager) (fileStorage.Storage, error) {
	var err error
	var storage fileStorage.Storage

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
	} else {
		return nil, ErrFileStorageTypeIsNotDefined
	}

	if err != nil {
		return nil, utl.NewFuncError(fileStorageInstance, err)
	}
	return storage, nil
}

// services
// -----------------------------------------------------------------------

func servicesInstance(
	tokenManager token.TokenManager,
	hashManager hash.HashManager,
	cacheInstance cache.Cache,
	domainStorage domainStorage.Storage,
	fileStorage fileStorage.Storage) (
	service.Services, error,
) {

	// deps only for services

	avatarGenerator, err := avatarGeneratorInstance()
	if err != nil {
		return nil, utl.NewFuncError(servicesInstance, err)
	}

	// ***

	services, err := serviceImpl.NewServices(serviceImpl.Dependencies{
		HashManager:  hashManager,
		TokenManager: tokenManager,

		AccessTokenTTL:  viper.GetDuration("delivery.token.access_ttl"),
		RefreshTokenTTL: viper.GetDuration("delivery.token.refresh_ttl"),

		EnableCache:     bool(cacheInstance != nil),
		Cache:           cacheInstance,
		DomainStorage:   domainStorage,
		FileStorage:     fileStorage,
		AvatarGenerator: avatarGenerator,

		GeoDistCalculator: geoDistanceImpl.NewCalculator(),

		GeneralParams: serviceImpl.GeneralParams{
			MaxPageSize: viper.GetUint64("service.general.pagination.max_page_size"),
		},

		AuthParams: serviceImpl.AuthParams{
			SignIn: serviceImpl.SignInParams{
				FailedAttemptCount: viper.GetUint64("service.auth.sign_in.failed_attempt_count"),
				FailedAttemptTtl:   viper.GetDuration("service.auth.sign_in.failed_attempt_ttl"),
				BlockingTime:       viper.GetDuration("service.auth.sign_in.blocking_time"),
			},
			SignUp: serviceImpl.SignUpParams{
				BlockingTime: viper.GetDuration("service.auth.sign_up.blocking_time"),
			},

			UsernamePattern: viper.GetString("service.auth.username_pattern"),
			PasswordPattern: viper.GetString("service.auth.password_pattern"),
		},

		ImageParams: serviceImpl.ImageParams{
			CacheTtl: viper.GetDuration("service.image.cache_ttl"),
			AddImageParams: serviceImpl.AddImageParams{
				BlockingTime: viper.GetDuration("service.image.add.blocking_time"),
			},
		},

		ChatParams: serviceImpl.ChatParams{
			MaxMessageLength: viper.GetUint64("service.chat.max_message_length"),
			GeoChatParams: serviceImpl.GeoChatParams{
				MaxMessageCountReturned: viper.GetUint64(
					"service.chat.geo.max_message_count_returned"),
			},
		},
	})

	if err != nil {
		return nil, utl.NewFuncError(servicesInstance, err)
	}
	return services, nil
}

// common
// -----------------------------------------------------------------------

func createCacheInstance() (cache.Cache, error) {
	cacheType := viper.GetString("cache.type")
	logger.Info("cache type: %v", cacheType)

	maxInitTime := viper.GetDuration("cache.max_init_time")
	ctxForInit, cancel := context.WithTimeout(context.Background(), maxInitTime)
	defer func() { cancel() }()

	var err error
	var cacheInstance cache.Cache
	if cacheType == "redis" {
		cacheInstance, err = redisCache.New(ctxForInit,
			redisCache.Dependencies{
				Host: viper.GetString("cache.redis.host"),
				Port: viper.GetUint16("cache.redis.port"),
				//...
			},
		)
	} else {
		return nil, ErrCacheTypeIsNotDefined
	}

	if err != nil {
		return nil, utl.NewFuncError(NewApp, err)
	}
	return cacheInstance, nil
}

func hashManagerInstance() (hash.HashManager, error) {
	hashType, err := hashImpl.StrToHashType(
		viper.GetString("storage.hash_type"))

	if err != nil {
		return nil, utl.NewFuncError(hashManagerInstance, err)
	}

	hashManager, err := hashImpl.NewHashManager(hashType)
	if err != nil {
		return nil, utl.NewFuncError(hashManagerInstance, err)
	}

	return hashManager, nil
}

func tokenManagerInstance() (token.TokenManager, error) {
	signingKey := viper.GetString("delivery.token.signing_key")
	tokenManager, err := tokenImpl.NewTokenManager(signingKey)
	if err != nil {
		return nil, utl.NewFuncError(tokenManagerInstance, err)
	}

	return tokenManager, nil
}

func avatarGeneratorInstance() (avatar.AvatarGenerator, error) {
	return avatarImpl.NewAvatarGenerator()
}
