package logger

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

type Logger interface {
	Trace(format string, a ...interface{})
	Debug(format string, a ...interface{})

	Info(format string, a ...interface{})
	Warning(format string, a ...interface{})

	Error(format string, a ...interface{})
	Fatal(format string, a ...interface{})

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

func Close() error {
	return instance.Close()
}
