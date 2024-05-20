package main

import (
	"common/pkg/logger"
	"context"
	"fmt"
	"geoqq_http/app"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func printlnWd() {
	wd, err := os.Getwd()
	if err != nil {
		log.Fatalf("get wd failed. Error: %v", err.Error())
		return // ?
	}

	fmt.Printf("wd: %v\n", wd)
}

func printlnExecutable() {
	path, err := os.Executable()
	if err != nil {
		log.Fatalf("get executable failed. Error: %v", err.Error())
		return // ?
	}

	fmt.Printf("executable path: %v\n", path)
}

func main() {

	printlnWd()

	printlnExecutable()

	// ***

	ctxWithCancel, cancel := context.WithCancel(
		context.Background(),
	)
	defer func() {
		cancel() // ?
	}()

	app, err := app.NewApp(ctxWithCancel)
	if err != nil {
		log.Fatalf("New app failed. Err: %v", err.Error())
	}

	go func() {
		err = app.Run()

		if err != nil {
			log.Fatalf("Run app failed. Err: %v", err.Error())
		}
	}()

	logger.Info("Server Is Running")

	// ***

	sigCh := make(chan os.Signal, 1)
	signal.Notify(sigCh,
		syscall.SIGINT,
		syscall.SIGHUP,
		syscall.SIGTERM,
		syscall.SIGABRT,
	)

	<-sigCh
	cancel()

	time.Sleep(250 * time.Millisecond) // not necessary!

	err = app.Stop()
	if err != nil {
		log.Fatalf("Stop app failed. Err: %v", err.Error())
	}
}
