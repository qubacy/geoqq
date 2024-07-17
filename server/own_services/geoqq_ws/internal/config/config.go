package config

import (
	"common/pkg/utility"
	"fmt"
	"os"

	"github.com/spf13/viper"
)

const (
	ConfigEnvPrefix = "GEOQQ_WS"

	FileName = "default"
	FileExt  = "yaml"
)

// -----------------------------------------------------------------------

var envKeyToViperKey = map[string]string{
	"LOGGING_FORMAT": "logging.format", // text, json

	"HOST": "adapters.interfaces.ws.host",
	"PORT": "adapters.interfaces.ws.port",

	"TOKEN_SIGNING_KEY": "adapters.interfaces.ws.token.signing_key",

	"POSTGRE_HOST": "adapters.infra.database.sql.postgre.host",
	"POSTGRE_PORT": "adapters.infra.database.sql.postgre.port",
	"POSTGRE_USER": "adapters.infra.database.sql.postgre.user",
	"POSTGRE_PASS": "adapters.infra.database.sql.postgre.password",
	"POSTGRE_DB":   "adapters.infra.database.sql.postgre.database",

	"CACHE_ENABLE": "adapters.infra.cache.enable",
	"REDIS_HOST":   "adapters.infra.cache.redis.host",
	"REDIS_PORT":   "adapters.infra.cache.redis.port",
	"REDIS_USER":   "adapters.infra.cache.redis.user",
	"REDIS_PASS":   "adapters.infra.cache.redis.password",
	"REDIS_DB":     "adapters.infra.cache.redis.db_index",

	"RABBIT_HOST":     "adapters.interfaces.msgg.rabbit.host",
	"RABBIT_PORT":     "adapters.interfaces.msgg.rabbit.port",
	"RABBIT_USERNAME": "adapters.interfaces.msgg.rabbit.username",
	"RABBIT_PASSWORD": "adapters.interfaces.msgg.rabbit.password",
	"RABBIT_EXCHANGE": "adapters.interfaces.msgg.rabbit.exchange_name",
}

// -----------------------------------------------------------------------

func Init() error {
	possibleConfigPaths := []string{
		".", "./config", "./internal/config",
		"../internal/config",
	}

	for _, p := range possibleConfigPaths {
		viper.AddConfigPath(p)
	}

	viper.SetConfigName(FileName)
	if err := viper.ReadInConfig(); err != nil {
		return utility.NewFuncError(Init, err)
	}

	mergeWithEnvironmentVars()

	fmt.Printf("rabbit username: %v\n", viper.GetString("adapters.interfaces.msgg.rabbit.username"))
	fmt.Printf("rabbit password: %v\n", viper.GetString("adapters.interfaces.msgg.rabbit.password"))

	fmt.Printf("postgre.host: %v\n", viper.GetString("adapters.infra.database.sql.postgre.host"))
	fmt.Printf("postgre.port: %v\n", viper.GetUint16("adapters.infra.database.sql.postgre.port"))

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
