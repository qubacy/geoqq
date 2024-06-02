package config

import (
	"common/pkg/utility"

	"github.com/spf13/viper"
)

const (
	FileName = "default"
	FileExt  = "yaml"
)

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
	return nil
}

func mergeWithEnvironmentVars() {

}
