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
}

var Instance Logger // TODO: to private!

// global funcs
// -----------------------------------------------------------------------

func Trace(format string, a ...interface{}) {
	Instance.Trace(format, a...)
}

func Debug(format string, a ...interface{}) {
	Instance.Debug(format, a...)
}

func Info(format string, a ...interface{}) {
	Instance.Info(format, a...)
}

func Warning(format string, a ...interface{}) {
	Instance.Warning(format, a...)
}

func Error(format string, a ...interface{}) {
	Instance.Error(format, a...)
}

func Fatal(format string, a ...interface{}) {
	Instance.Fatal(format, a...)

	//os.Exit(1)
}
