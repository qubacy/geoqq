package main

import (
	"geoqq/app"
	"log"
	"os"
)

func printlnWd() {
	pwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Get wd failed. Err: %v", err.Error())
	}

	log.Println(pwd)
}

func printlnExecutable() {
	path, err := os.Executable()
	if err != nil {
		log.Fatalf("Get executable failed. Err: %v", err.Error())
	}

	log.Println(path)
}

func main() {

	printlnWd()
	printlnExecutable()

	// ***

	app, err := app.NewApp()
	if err != nil {
		log.Fatalf("New app failed. Err: %v", err.Error())
	}

	err = app.Run()
	if err != nil {
		log.Fatalf("Run app failed. Err: %v", err.Error())
	}
}
