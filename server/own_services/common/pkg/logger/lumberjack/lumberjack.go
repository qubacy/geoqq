package lumberjack

import (
	"common/pkg/logger"
	"fmt"
	"io"
	"log"
	"os"
	"sync"
	"time"

	ljack "gopkg.in/natefinch/lumberjack.v2"
)

type LumberjackLogger struct {
	name   string
	level  logger.Level
	format logger.Format

	mx     sync.Mutex
	logger *ljack.Logger
}

// ctor
// -----------------------------------------------------------------------

func SetLumberjackLoggerForStdLog(
	name string, level logger.Level, format logger.Format, useConsole bool,
	dirname, filename string, maxSizeMB int, maxBackups int, maxAgeDays int,
) *LumberjackLogger {

	ll := &ljack.Logger{
		Filename:   dirname + "/" + filename,
		MaxSize:    maxSizeMB,
		MaxBackups: maxBackups,
		MaxAge:     maxAgeDays,

		Compress:  false,
		LocalTime: false,
	}

	// ***

	multiWriter := io.MultiWriter(ll)
	if useConsole {
		multiWriter = io.MultiWriter(os.Stdout, multiWriter)
	}
	log.SetOutput(multiWriter)

	log.SetFlags(0)
	if format != logger.FormatJson {
		log.SetFlags(log.Ldate | log.Ltime | log.Lmicroseconds)
	}

	// ***

	resultLogger := &LumberjackLogger{
		name:   name,
		level:  level, // error and fatal always!
		format: format,

		mx:     sync.Mutex{},
		logger: ll,
	}
	return resultLogger
}

// public
// -----------------------------------------------------------------------

func (l *LumberjackLogger) Output() io.Writer {
	return log.Writer() // see constructor!
}

func (l *LumberjackLogger) Close() error {
	return l.logger.Close()
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) Trace(format string, a ...interface{}) {
	if l.level > logger.LevelTrace {
		return
	}
	l.printf(logger.LevelTrace, format, a...)
}

func (l *LumberjackLogger) Debug(format string, a ...interface{}) {
	if l.level > logger.LevelDebug {
		return
	}
	l.printf(logger.LevelDebug, format, a...)
}

func (l *LumberjackLogger) Info(format string, a ...interface{}) {
	if l.level > logger.LevelInfo {
		return
	}
	l.printf(logger.LevelInfo, format, a...)
}

func (l *LumberjackLogger) Warning(format string, a ...interface{}) {
	if l.level > logger.LevelWarning {
		return
	}
	l.printf(logger.LevelWarning, format, a...)
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) Error(format string, a ...interface{}) {
	l.printf(logger.LevelError, format, a...)
}

func (l *LumberjackLogger) Fatal(format string, a ...interface{}) {
	l.fatalf(logger.LevelFatal, format, a...) // !
}

// private
// -----------------------------------------------------------------------

const (
	stackFrameCountToSkip = 4
)

func (l *LumberjackLogger) makeLogEntry(
	level logger.Level, format string, a ...interface{},
) string {

	caller := logger.MakeCallerInfo(stackFrameCountToSkip)
	if l.format == logger.FormatJson {
		le := logger.JsonLogEntry{
			Name:    l.name,
			Time:    time.Now().UTC().String(),
			Level:   level.String(),
			Caller:  caller.String(),
			Message: fmt.Sprintf(format, a...),
		}
		return le.ToJsonString()
	}

	return fmt.Sprintf("%v | %v | %v",
		caller.String(), level.String(),
		fmt.Sprintf(format, a...),
	)
}

// -----------------------------------------------------------------------

func (l *LumberjackLogger) printf(lvl logger.Level, format string, a ...interface{}) {
	l.mx.Lock()
	defer l.mx.Unlock()

	log.Print(l.makeLogEntry(lvl, format, a...))
}

func (l *LumberjackLogger) fatalf(lvl logger.Level, format string, a ...interface{}) {
	l.mx.Lock()
	defer l.mx.Unlock()

	log.Fatal(l.makeLogEntry(lvl, format, a...))
}
