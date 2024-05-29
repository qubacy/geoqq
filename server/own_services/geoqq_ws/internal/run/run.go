package toggle

import (
	"os"
	"os/signal"
	"syscall"
)

func Do() error {

	exit := shutdown()

	// config

	// logger

	// ***

	// infrastructure/output

	// application core

	// interfaces/input

	// gracefully shutdown

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
