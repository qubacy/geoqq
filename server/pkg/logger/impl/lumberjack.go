package impl

import (
	"geoqq/pkg/logger"
	"log"
	"sync"

	"gopkg.in/natefinch/lumberjack.v2"
)

type LumberjackLogger struct {
	mx    sync.Mutex
	level logger.Level
}

// ctor
// -----------------------------------------------------------------------

func SetLumberjackLoggerForStdOutput(
	level logger.Level, dirname, filename string,
	maxSizeMB int, maxBackups int, maxAgeDays int,
) *LumberjackLogger {

	log.SetOutput(&lumberjack.Logger{
		Filename:   dirname + "/" + filename,
		MaxSize:    maxSizeMB,
		MaxBackups: maxBackups,
		MaxAge:     maxAgeDays,

		Compress:  false,
		LocalTime: false,
	})

	log.SetFlags(
		log.Ldate | log.Ltime | log.Lmicroseconds |
			log.LUTC,
	)

	return &LumberjackLogger{
		level: level, // error and fatal always!
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
