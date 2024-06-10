package main

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"geoqq_ws/internal/toggle"
)

func main() {
	if err := toggle.Do(); err != nil {
		logger.Fatal("%v", utl.NewFuncError(main, err))
	}
}
