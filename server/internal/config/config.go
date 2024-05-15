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
	ConfigEnvPrefix = "GEOQQ_HTTP"

	ConfigFileName = "config"
	ConfigFileExt  = "yml"
)

var (
	configFullFn = strings.Join(
		[]string{ConfigFileName, ConfigFileExt}, ".")

	configPath = ""
)

func Initialize() error {
	var err error = nil
	configPath, err = currentConfigPathToFile() // find default yaml!

	if err != nil {
		return utl.NewFuncError(Initialize, err)
	}
	fmt.Printf("config path: %v\n", configPath)

	// weak priority (yml)

	viper.AddConfigPath(configPath)
	viper.SetConfigName(ConfigFileName)

	if err = viper.ReadInConfig(); err != nil {
		return utl.NewFuncError(Initialize, err)
	}

	// medium (config_for_merge.yaml)

	if partConfig := os.Getenv(envar("CONFIG_FOR_MERGE")); partConfig != "" {
		viper.AddConfigPath(configPath + "/for_merge")

		viper.SetConfigName(partConfig)
		viper.MergeInConfig()
	}

	// medium (env file)

	// high (environment variables)

	mergeWithEnvironmentVars()
	return nil
}

func mergeWithEnvironmentVars() {
	if host := os.Getenv(envar("HOST")); host != "" {
		viper.Set("server.http.host", host)
	}
	if port := os.Getenv("PORT"); port != "" {
		viper.Set("server.http.port", port)
	}

	if postgreHost := os.Getenv("POSTGRE_HOST"); len(postgreHost) != 0 {
		viper.Set("storage.domain.sql.postgre.host", postgreHost)
	}
	if postgrePort := os.Getenv("POSTGRE_PORT"); len(postgrePort) != 0 {
		viper.Set("storage.domain.sql.postgre.host", postgrePort)
	}
}

// private
// -----------------------------------------------------------------------

func envar(name string) string {
	return ConfigEnvPrefix + "_" + name
}

func wholeConfigFileName(rootCatalog string) string {
	return rootCatalog + "/" + configFullFn
}

func existsConfigFile(rootCatalog string) bool {
	whole := wholeConfigFileName(rootCatalog)
	if _, err := os.Stat(whole); errors.Is(err, os.ErrNotExist) {
		return false
	}
	return true
}

func currentConfigPathToFile() (string, error) {
	possibleRootCatalogs := []string{
		".", "./config",
		"./internal/config",
		"../internal/config",
	}

	for _, rootCatalog := range possibleRootCatalogs {
		if existsConfigFile(rootCatalog) {
			return rootCatalog, nil
		}
	}

	return "", ErrConfigFileNotFound
}
