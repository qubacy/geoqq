package config

import (
	"errors"
	"fmt"
	"os"
	"strings"

	utl "geoqq/pkg/utility"

	"github.com/joho/godotenv"
	"github.com/spf13/viper"
)

const (
	ConfigEnvPrefix = "GEOQQ_HTTP"

	ConfigFileName = "config"
	ConfigFileExt  = "yml"
)

// -----------------------------------------------------------------------

var (
	configFullFn = strings.Join(
		[]string{ConfigFileName, ConfigFileExt}, ".")

	configPath = ""
)

var envKeyToViperKey = map[string]string{
	"HOST": "server.http.host",
	"PORT": "server.http.port",

	"TOKEN_SIGNING_KEY": "delivery.token.signing_key",
	"ACCESS_TOKEN_TTL":  "delivery.token.access_ttl",
	"REFRESH_TOKEN_TTL": "delivery.token.refresh_ttl",

	"POSTGRE_HOST": "storage.domain.sql.postgre.host",
	"POSTGRE_PORT": "storage.domain.sql.postgre.port",
	"POSTGRE_USER": "storage.domain.sql.postgre.user",
	"POSTGRE_PASS": "storage.domain.sql.postgre.password",
	"POSTGRE_DB":   "storage.domain.sql.postgre.database",

	"CACHE_ENABLE": "cache.enable",
	"REDIS_HOST":   "cache.redis.host",
	"REDIS_PORT":   "cache.redis.port",
	"REDIS_USER":   "cache.redis.user",
	"REDIS_PASS":   "cache.redis.password",
	"REDIS_DB":     "cache.redis.db_index",
}

// -----------------------------------------------------------------------

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

	// medium 1 (config_for_merge.yaml)

	if partConfig := os.Getenv(envar("CONFIG_FOR_MERGE")); partConfig != "" {
		viper.AddConfigPath(configPath + "/for_merge")

		viper.SetConfigName(partConfig)
		viper.MergeInConfig()
	}

	// medium 2 (env file)

	if err = mergeWithEnvironmentFile(); err != nil {
		fmt.Printf(".env not found")
	}

	// high (environment variables)

	mergeWithEnvironmentVars()

	return nil
}

func mergeWithEnvironmentFile() error {
	var envFromFile map[string]string
	envFromFile, err := godotenv.Read()
	if err != nil {
		return utl.NewFuncError(mergeWithEnvironmentFile, err)
	}

	for envKey, viperKey := range envKeyToViperKey {
		value, exists := envFromFile[envKey]
		if exists {
			viper.Set(viperKey, value)
		}
	}

	return nil
}

func mergeWithEnvironmentVars() {
	for envKey, viperKey := range envKeyToViperKey {
		if value := os.Getenv(envar(envKey)); value != "" {
			viper.Set(viperKey, value)
		}
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
