package logger

import (
	"io"
	"runtime"
	"strings"
)

type Level int

const (
	LevelTrace Level = iota
	LevelDebug

	LevelInfo
	LevelWarning

	LevelError
	LevelFatal
)

func (l Level) String() string {
	switch l {
	case LevelDebug:
		return "DEBUG"

	case LevelInfo:
		return "INFO"
	case LevelWarning:
		return "WARNING"

	case LevelError:
		return "ERROR"
	case LevelFatal:
		return "FATAL"
	}

	// LevelTrace or unknown?
	return "TRACE"
}

type CallerInfo struct {
	FullFileName  string
	ShortFileName string
	Line          int
}

func MakeCallerInfo(skip int) CallerInfo {
	_, file, line, ok := runtime.Caller(skip + 1)
	if !ok || len(file) == 0 { // ?

		// ok is false if it was not possible
		// to recover the information.

		return CallerInfo{
			FullFileName:  "_",
			ShortFileName: "_",
			Line:          -1,
		}
	}

	// os.PathSeparator not working!
	sep := "/"
	parts := strings.Split(file, sep)
	shortFileName := parts[len(parts)-1]
	if len(parts) > 1 {
		shortFileName = strings.Join(parts[len(parts)-2:], sep)
	}

	return CallerInfo{
		FullFileName:  file,
		ShortFileName: shortFileName,
		Line:          line,
	}
}

type Logger interface {
	Trace(format string, a ...interface{})
	Debug(format string, a ...interface{})

	Info(format string, a ...interface{})
	Warning(format string, a ...interface{})

	Error(format string, a ...interface{})
	Fatal(format string, a ...interface{})

	Output() io.Writer
	Close() error
}

// initialize
// -----------------------------------------------------------------------

var instance Logger = nil

func Initialize(lr Logger) error {
	if instance != nil {
		return ErrLoggerIsAlreadyInitialized
	}

	instance = lr
	return nil
}

// global funcs available after initialization!
// -----------------------------------------------------------------------

func To(l Level, format string, a ...interface{}) {
	switch l {
	case LevelTrace:
		Trace(format, a...)
	case LevelDebug:
		Debug(format, a...)

	case LevelInfo:
		Info(format, a...)
	case LevelWarning:
		Warning(format, a...)

	case LevelError:
		Error(format, a...)
	case LevelFatal:
		Fatal(format, a...)

	default:
		Trace(format, a...) // !
	}
}

func Trace(format string, a ...interface{}) {
	instance.Trace(format, a...)
}

func Debug(format string, a ...interface{}) {
	instance.Debug(format, a...)
}

func Info(format string, a ...interface{}) {
	instance.Info(format, a...)
}

func Warning(format string, a ...interface{}) {
	instance.Warning(format, a...)
}

func Error(format string, a ...interface{}) {
	instance.Error(format, a...)
}

func Fatal(format string, a ...interface{}) {
	instance.Fatal(format, a...)

	//os.Exit(1)
}

// -----------------------------------------------------------------------

func Output() io.Writer {
	return instance.Output()
}

func Close() error {
	return instance.Close()
}
