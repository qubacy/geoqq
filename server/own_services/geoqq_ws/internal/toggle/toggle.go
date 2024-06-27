package toggle

import (
	geoCalculatorImpl "common/pkg/geoDistance/haversine"
	"common/pkg/logger"
	"common/pkg/logger/lumberjack"
	"common/pkg/token"
	tokenImpl "common/pkg/token/cristalJwt"
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_ws/internal/adapters/infrastructure/cache/redis"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/background"
	"geoqq_ws/internal/adapters/interfaces/wsApi"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/usecase"
	"geoqq_ws/internal/config"
	"geoqq_ws/internal/constErrors"
	"os"
	"os/signal"
	"syscall"

	"github.com/spf13/viper"
)

func Do() error {

	exit := shutdown()

	// config

	if err := config.Init(); err != nil {
		return utl.NewFuncError(Do, err)
	}
	fmt.Printf("config initialized\n")

	// logger

	if err := initializeLogging(); err != nil {
		return utl.NewFuncError(Do, err)
	}

	// ***

	startTimeout := viper.GetDuration("start_timeout")
	stopTimeout := viper.GetDuration("stop_timeout")

	startCtx, startCancel := context.WithTimeout(
		context.Background(), startTimeout)
	defer startCancel()

	// infrastructure/output

	db, err := postgre.New(startCtx, postgre.Params{
		Host:         viper.GetString("adapters.infra.database.sql.postgre.host"),
		Port:         viper.GetUint16("adapters.infra.database.sql.postgre.port"),
		Username:     viper.GetString("adapters.infra.database.sql.postgre.user"),
		Password:     viper.GetString("adapters.infra.database.sql.postgre.password"),
		DatabaseName: viper.GetString("adapters.infra.database.sql.postgre.database"),
	}, background.Params{
		MaxWorkerCount: viper.GetInt("adapters.infra.database.sql.postgre.background.max_worker_count"),
		MaxQueryCount:  viper.GetInt("adapters.infra.database.sql.postgre.background.max_query_count"),
		QueryTimeout:   viper.GetDuration("adapters.infra.database.sql.postgre.background.query_timeout"),
	})
	if err != nil {
		return utl.NewFuncError(Do, err)
	}
	tempDb, err := redis.New(startCtx, &redis.Params{
		Host:     viper.GetString("adapters.infra.cache.redis.host"),
		Port:     viper.GetUint16("adapters.infra.cache.redis.port"),
		User:     viper.GetString("adapters.infra.cache.redis.user"),
		Password: viper.GetString("adapters.infra.cache.redis.password"),
		DbIndex:  viper.GetInt("adapters.infra.cache.redis.db_index"),
	})
	if err != nil {
		return utl.NewFuncError(Do, err)
	}

	// application core

	var (
		userUc        input.UserUsecase = nil
		onlineUsersUc input.OnlineUsersUsecase
		mateMessageUc input.MateMessageUsecase
		geoMessageUc  input.GeoMessageUsecase
	)
	{
		commonParams := struct {
			MaxLength uint64
		}{
			MaxLength: viper.GetUint64("usecase.common.chat_message.max_length"),
		}

		// ***

		userUc = usecase.NewUserUsecase(&usecase.UserUcParams{
			Database:     db,
			TempDatabase: tempDb,
		})
		onlineUsersUc = usecase.NewOnlineUsersUsecase(&usecase.OnlineUsersParams{
			TempDatabase:        tempDb,
			CacheRequestTimeout: viper.GetDuration("adapters.infra.cache.req_timeout"),
		})
		mateMessageUc = usecase.NewMateMessageUsecase(&usecase.MateMessageUcParams{
			OnlineUsersUc: onlineUsersUc,
			Database:      db,

			FbChanSize:  viper.GetInt("usecase.mate_message.fb_chan_size"),
			FbChanCount: viper.GetInt("usecase.mate_message.fb_chan_count"),

			MaxMessageLength: commonParams.MaxLength,
		})
		geoMessageUc = usecase.NewGeoMessageUsecase(&usecase.GeoMessageUcParams{
			OnlineUsersUc: onlineUsersUc,
			Database:      db,
			TempDatabase:  tempDb,

			FbChanSize:  viper.GetInt("usecase.geo_message.fb_chan_size"),
			FbChanCount: viper.GetInt("usecase.geo_message.fb_chan_count"),

			MaxMessageLength: commonParams.MaxLength,
			MaxRadius:        viper.GetUint64("usecase.geo_message.max_radius"),
			GeoCalculator:    geoCalculatorImpl.NewCalculator(),
		})
	}

	// interfaces/input

	tokenManager, err := tokenManagerForWsInput()
	if err != nil {
		return utl.NewFuncError(Do, err)
	}

	wsServer, err := wsApi.New(&wsApi.Params{
		Host:        viper.GetString("adapters.interfaces.ws.host"),
		Port:        viper.GetUint16("adapters.interfaces.ws.port"),
		MaxHeaderKb: viper.GetInt("adapters.interfaces.ws.max_header_kb"),

		ReadTimeout:   viper.GetDuration("adapters.interfaces.ws.read_timeout"),
		WriteTimeout:  viper.GetDuration("adapters.interfaces.ws.write_timeout"),
		HandleTimeout: viper.GetDuration("adapters.interfaces.ws.handle_timeout"),

		EnablePing:  viper.GetBool("adapters.interfaces.ws.ping.enable"),
		PingTimeout: viper.GetDuration("adapters.interfaces.ws.ping.timeout"),

		TpExtractor: tokenManager,

		UserUc:        userUc,
		OnlineUsersUc: onlineUsersUc,
		MateMessageUc: mateMessageUc,
		GeoMessageUc:  geoMessageUc,
	})
	if err != nil {
		return utl.NewFuncError(Do, err)
	}
	go func() {
		if err := wsServer.Listen(); err != nil {
			logger.Fatal("%v", utl.NewFuncError(Do, err)) // !
		}
	}()

	<-exit

	// stop all...

	stopCtx, stopCancel := context.WithTimeout(
		context.Background(), stopTimeout)
	defer stopCancel()

	if err = wsServer.Stop(stopCtx); err != nil {
		logger.Error("%v", utl.NewFuncError(Do, err))
	}

	return nil
}

func shutdown() <-chan os.Signal {
	exitCh := make(chan os.Signal, 1)

	signal.Notify(exitCh,
		syscall.SIGINT,
		syscall.SIGHUP,
		syscall.SIGTERM,
		syscall.SIGABRT,
	)

	return exitCh
}

// -----------------------------------------------------------------------

func initializeLogging() error {
	loggingType := viper.GetString("logging.type")
	if loggingType == "lumberjack" {
		logger.Initialize(lumberjack.SetLumberjackLoggerForStdLog(
			viper.GetString("logging.name"),
			logger.Level(viper.GetInt("logging.level")),
			logger.FormatFromStr(viper.GetString("logging.format")), // text, json

			viper.GetBool("logging.use_console"),

			viper.GetString("logging.dirname"),
			viper.GetString("logging.filename"),

			viper.GetInt("logging.max_size_mb"),
			viper.GetInt("logging.max_backups"), // amount!

			viper.GetInt("logging.lumberjack.max_age_days")))

	} else if loggingType == "mlog" {
		return constErrors.ErrNotImplemented
	} else if loggingType == "zkits" {
		return constErrors.ErrNotImplemented
	} else {

		return ErrLoggingTypeIsNotDefined
	}

	// ***

	logger.Trace("trace")
	logger.Debug("debug")
	logger.Info("info")
	logger.Warning("warning")
	logger.Error("error")

	return nil
}

func tokenManagerForWsInput() (token.TokenManager, error) {
	signingKey := viper.GetString("adapters.interfaces.ws.token.signing_key") // ?
	tokenManager, err := tokenImpl.NewTokenManager(signingKey)
	if err != nil {
		return nil, utl.NewFuncError(tokenManagerForWsInput, err)
	}

	return tokenManager, nil
}
