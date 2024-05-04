package impl

import (
	"fmt"
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
		log.Ldate | log.Ltime | log.Lmicroseconds,
	)

	return &LumberjackLogger{
		mx:     sync.Mutex{},
		level:  level, // error and fatal always!
		logger: logger,
	}
}

// public
// -----------------------------------------------------------------------

var stackFrameCountToSkip int = 2

func (l *LumberjackLogger) Trace(format string, a ...interface{}) {
	if logger.LevelTrace < l.level {
		return
	}

	l.printf(logger.MakeCallerInfo(stackFrameCountToSkip),
		logger.LevelTrace, format, a...)
}

func (l *LumberjackLogger) Debug(format string, a ...interface{}) {
	if logger.LevelDebug < l.level {
		return
	}

	l.printf(logger.MakeCallerInfo(stackFrameCountToSkip),
		logger.LevelDebug, format, a...)
}

func (l *LumberjackLogger) Info(format string, a ...interface{}) {
	if logger.LevelInfo < l.level {
		return
	}

	l.printf(logger.MakeCallerInfo(stackFrameCountToSkip),
		logger.LevelInfo, format, a...)
}

func (l *LumberjackLogger) Warning(format string, a ...interface{}) {
	if logger.LevelWarning < l.level {
		return
	}

	l.printf(logger.MakeCallerInfo(stackFrameCountToSkip),
		logger.LevelWarning, format, a...)
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) Error(format string, a ...interface{}) {
	l.printf(logger.MakeCallerInfo(stackFrameCountToSkip),
		logger.LevelError, format, a...)
}

func (l *LumberjackLogger) Fatal(format string, a ...interface{}) {
	l.mx.Lock() // ?
	defer l.mx.Unlock()

	caller := logger.MakeCallerInfo(stackFrameCountToSkip)
	log.Fatalf(join(caller, logger.LevelFatal, format), a...)
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

func (l *LumberjackLogger) printf(caller logger.CallerInfo, lvl logger.Level, format string, a ...interface{}) {
	l.mx.Lock()
	defer l.mx.Unlock()

	log.Printf(join(caller, lvl, format), a...)
}

func join(caller logger.CallerInfo, level logger.Level, format string) string {
	return fmt.Sprintf("%v:%v | %v | %v",
		caller.ShortFileName, caller.Line,
		level.String(), format,
	)
}
