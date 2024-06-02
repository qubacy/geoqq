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
