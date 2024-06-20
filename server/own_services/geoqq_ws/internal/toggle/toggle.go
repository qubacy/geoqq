package toggle

import (
	"common/pkg/logger"
	"common/pkg/logger/lumberjack"
	"common/pkg/token"
	tokenImpl "common/pkg/token/cristalJwt"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre"
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

	// other deps

	tokenManager, err := tokenManagerInstance()
	if err != nil {
		return utl.NewFuncError(Do, err)
	}

	// infrastructure/output

	db, err := postgre.New(startCtx, postgre.Dependencies{
		Host:         viper.GetString("adapters.infra.database.sql.postgre.host"),
		Port:         viper.GetUint16("adapters.infra.database.sql.postgre.port"),
		Username:     viper.GetString("adapters.infra.database.sql.postgre.user"),
		Password:     viper.GetString("adapters.infra.database.sql.postgre.password"),
		DatabaseName: viper.GetString("adapters.infra.database.sql.postgre.database"),
	})
	if err != nil {
		return utl.NewFuncError(Do, err)
	}

	// application core

	var userUc input.UserUsecase = usecase.NewUserUsecase(usecase.UserUcParams{
		Database: db,
	})
	var onlineUsersUc = usecase.NewOnlineUsersUsecase()
	var mateMessageUc = usecase.NewMateMessageUsecase(usecase.MateMessageUcParams{
		OnlineUsersUc: onlineUsersUc,
		Database:      db,

		FbChanSize:  viper.GetInt("usecase.mate_message.fb_chan_size"),
		FbChanCount: viper.GetInt("usecase.mate_message.fb_chan_count"),
	})

	// interfaces/input

	wsServer, err := wsApi.New(&wsApi.Params{
		Host:        viper.GetString("adapters.interfaces.ws.host"),
		Port:        viper.GetUint16("adapters.interfaces.ws.port"),
		MaxHeaderKb: viper.GetInt("adapters.interfaces.ws.max_header_kb"),

		ReadTimeout:  viper.GetDuration("adapters.interfaces.ws.read_timeout"),
		WriteTimeout: viper.GetDuration("adapters.interfaces.ws.write_timeout"),

		EnablePing:  viper.GetBool("adapters.interfaces.ws.ping.enable"),
		PingTimeout: viper.GetDuration("adapters.interfaces.ws.ping.timeout"),

		TpExtractor: tokenManager,

		UserUc:        userUc,
		OnlineUsersUc: onlineUsersUc,
		MateMessageUc: mateMessageUc,
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

//TODO: to common func!!!

func initializeLogging() error {
	loggingType := viper.GetString("logging.type")
	if loggingType == "lumberjack" {
		logger.Initialize(lumberjack.SetLumberjackLoggerForStdLog(
			viper.GetString("logging.name"),
			logger.Level(viper.GetInt("logging.level")),
			logger.FormatFromStr(viper.GetString("logging.format")),

			viper.GetBool("logging.use_console"),

			viper.GetString("logging.dirname"),
			viper.GetString("logging.filename"),

			viper.GetInt("logging.max_size_mb"),
			viper.GetInt("logging.max_backups"),
			viper.GetInt("logging.lumberjack.max_age_days"),
		))
	} else if loggingType == "mlog" {
		//...
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

func tokenManagerInstance() (token.TokenManager, error) {
	signingKey := viper.GetString("adapters.interfaces.ws.token.signing_key") // ?
	tokenManager, err := tokenImpl.NewTokenManager(signingKey)
	if err != nil {
		return nil, utl.NewFuncError(tokenManagerInstance, err)
	}

	return tokenManager, nil
}
