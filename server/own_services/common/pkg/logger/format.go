package logger

import (
	"encoding/json"
	"fmt"
)

type Format int

const (
	FormatText = iota
	FormatJson
)

func FormatFromStr(v string) Format {
	switch v {
	case "json":
		return FormatJson
	default:
		return FormatText
	}
}

type JsonLogEntry struct {
	Name    string `json:"name"`
	Time    string `json:"time"`
	Level   string `json:"level"`
	Caller  string `json:"caller"`
	Message string `json:"message"`
}

func (j *JsonLogEntry) ToJsonString() string {
	jsonBytes, err := json.Marshal(j)
	if err != nil { // ---> impossible!

		// if this is possible,
		// then you can notice it during local testing...

		return fmt.Sprintf(
			`{ "entry": "%v", "marshal_error:": "%v" }`, j, err,
		)
	}

	return string(jsonBytes)
}
