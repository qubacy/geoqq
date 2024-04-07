package impl

import (
	"geoqq/pkg/logger"
	"testing"
)

func Test_Log(t *testing.T) {
	lr := SetLumberjackLoggerForStdOutput(
		logger.LevelDebug, "./lumberjack", "test.log", 1, 3, 1,
	)

	for i := 0; i < 100000; i++ {
		lr.Debug("Lorem ipsum!")
		lr.Info("Lorem ipsum!")
		//...
	}
}
