package config

import (
	"errors"
	"fmt"
	"os"
	"strings"

	utl "geoqq/pkg/utility"

	"github.com/spf13/viper"
)

const (
	ConfigFileName = "config"
	ConfigFileExt  = "yml"
)

func Initialize() error {
	configPath, err := currentConfigPathToFile()
	if err != nil {
		return utl.NewFuncError(Initialize, err)
	}
	fmt.Printf("config path: %v\n", configPath)

	// ***

	viper.AddConfigPath(configPath)
	viper.SetConfigName(ConfigFileName)

	if err = viper.ReadInConfig(); err != nil {
		return utl.NewFuncError(Initialize, err)
	}

	// ***

	if host := os.Getenv("HTTP_HOST"); len(host) != 0 {
		viper.Set("server.http.host", host)
	}
	if port := os.Getenv("HTTP_PORT"); len(port) != 0 {
		viper.Set("server.http.port", port)
	}

	return nil
}

// private
// -----------------------------------------------------------------------

func wholeConfigFileName(rootCatalog string) string {
	return rootCatalog + "/" +
		strings.Join([]string{ConfigFileName, ConfigFileExt}, ".")
}

func existsConfigFile(rootCatalog string) bool {
	whole := wholeConfigFileName(rootCatalog)
	if _, err := os.Stat(whole); errors.Is(err, os.ErrNotExist) {
		return false
	}
	return true
}

func currentConfigPathToFile() (string, error) {
	rootCatalog := "."
	if existsConfigFile(".") {
		return rootCatalog, nil
	}

	rootCatalog = "../internal/config"
	if existsConfigFile(rootCatalog) {
		return rootCatalog, nil
	}

	rootCatalog = "./internal/config"
	if existsConfigFile(rootCatalog) {
		return rootCatalog, nil
	}

	return "", ErrConfigNotFound
}

// -----------------------------------------------------------------------
