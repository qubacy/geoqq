package logger

import (
	"io"
)

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

func Initialized() bool {
	return instance != nil
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
