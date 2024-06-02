package lumberjack

import (
	"common/pkg/logger"
	"io"
	"os"
	"testing"
)

func Test_Log(t *testing.T) {
	lr := SetLumberjackLoggerForStdLog(
		"test", logger.LevelDebug, logger.FormatJson, false,
		"./logs", "test.log",
		1, 3, 1,
	)

	for i := 0; i < 100; i++ {
		lr.Debug("Lorem ipsum!")
		lr.Info("Lorem ipsum!")
		//...
	}
}

func Test_MultiWriter(t *testing.T) {
	writer := io.MultiWriter(os.Stdout)
	writer.Write([]byte("Hi!"))

	writer = io.MultiWriter(writer)
	writer.Write([]byte("Hi!"))

	//...
}
