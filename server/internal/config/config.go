package config

import (
	"errors"
	"log"
	"os"
	"strings"

	"github.com/spf13/viper"
)

const (
	ConfigFileName = "config"
	ConfigFileExt  = "yml"
)

func Initialize() error {
	configPath, err := currentConfigPathToFile()
	if err != nil {
		// TODO: build default config!

		configPath = "."
		return err
	}

	log.Println("Config path:", configPath)

	viper.AddConfigPath(configPath)
	viper.SetConfigName(ConfigFileName)

	return viper.ReadInConfig()
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

// temp solution!
func currentConfigPathToFile() (string, error) {
	cur := "."
	if existsConfigFile(".") {
		return cur, nil
	}

	cur = "../internal/config"
	if existsConfigFile(cur) {
		return cur, nil
	}

	return "", ErrConfigNotFound
}
