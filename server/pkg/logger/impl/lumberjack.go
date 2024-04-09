package impl

import (
	"geoqq/pkg/logger"
	"io"
	"log"
	"os"
	"sync"

	"gopkg.in/natefinch/lumberjack.v2"
)

type LumberjackLogger struct {
	mx     sync.Mutex
	level  logger.Level
	logger *lumberjack.Logger
}

// ctor
// -----------------------------------------------------------------------

func SetLumberjackLoggerForStdOutput(
	level logger.Level, useConsole bool, dirname, filename string,
	maxSizeMB int, maxBackups int, maxAgeDays int,
) *LumberjackLogger {

	logger := &lumberjack.Logger{
		Filename:   dirname + "/" + filename,
		MaxSize:    maxSizeMB,
		MaxBackups: maxBackups,
		MaxAge:     maxAgeDays,

		Compress:  false,
		LocalTime: false,
	}

	multiWriter := io.MultiWriter(logger)
	if useConsole {
		multiWriter = io.MultiWriter(os.Stdout, multiWriter)
	}
	log.SetOutput(multiWriter)

	log.SetFlags(
		log.Ldate | log.Ltime | log.Lmicroseconds |
			log.LUTC,
	)

	return &LumberjackLogger{
		mx:     sync.Mutex{},
		level:  level, // error and fatal always!
		logger: logger,
	}
}

// public
// -----------------------------------------------------------------------

func (l *LumberjackLogger) Trace(format string, a ...interface{}) {
	if logger.LevelTrace >= l.level {
		l.printf(logger.LevelTrace, format, a...)
	}
}

func (l *LumberjackLogger) Debug(format string, a ...interface{}) {
	if logger.LevelDebug >= l.level {
		l.printf(logger.LevelDebug, format, a...)
	}
}

func (l *LumberjackLogger) Info(format string, a ...interface{}) {
	if logger.LevelInfo >= l.level {
		l.printf(logger.LevelInfo, format, a...)
	}
}

func (l *LumberjackLogger) Warning(format string, a ...interface{}) {
	if logger.LevelWarning >= l.level {
		l.printf(logger.LevelWarning, format, a...)
	}
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) Error(format string, a ...interface{}) {
	l.printf(logger.LevelError, format, a...)
}

func (l *LumberjackLogger) Fatal(format string, a ...interface{}) {
	l.mx.Lock() // ?
	defer l.mx.Unlock()

	log.Fatalf(join(logger.LevelFatal, format), a...)
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) Output() io.Writer {
	return log.Writer() // see constructor!
}

func (l *LumberjackLogger) Close() error {
	return l.logger.Close()
}

// private
// -----------------------------------------------------------------------

func (l *LumberjackLogger) printf(lvl logger.Level, format string, a ...interface{}) {
	l.mx.Lock()
	defer l.mx.Unlock()

	log.Printf(join(lvl, format), a...)
}

func join(level logger.Level, format string) string {
	return level.String() + " | " + format
}
