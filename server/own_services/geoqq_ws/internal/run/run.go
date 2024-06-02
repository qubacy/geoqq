package toggle

import (
	utl "common/pkg/utility"
	"geoqq_ws/internal/config"
	"os"
	"os/signal"
	"syscall"
)

func Do() error {

	exit := shutdown()

	// config

	if err := config.Init(); err != nil {
		return utl.NewFuncError(Do, err)
	}

	// logger

	// ***

	// infrastructure/output

	// application core

	// interfaces/input

	<-exit

	// stop all!

	return nil
}

// -----------------------------------------------------------------------

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
