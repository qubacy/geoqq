package main

import (
	"geoqq/app"
	"log"
)

func main() {
	app, err := app.NewApp()
	if err != nil {
		log.Fatalf("New app failed. Err: %v", err.Error())
	}

	err = app.Run()
	if err != nil {
		log.Fatalf("Run app failed. Err: %v", err.Error())
	}
}
