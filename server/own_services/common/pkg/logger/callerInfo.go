package logger

import (
	"fmt"
	"runtime"
	"strings"
)

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

func (c *CallerInfo) String() string {
	return fmt.Sprintf("%v:%v", c.ShortFileName, c.Line)
}
