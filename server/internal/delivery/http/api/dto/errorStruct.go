package dto

type ResWithError struct {
	Error `json:"error"`
}

func MakeResWithError(id int, shortErr error) ResWithError {
	return ResWithError{
		Error: Error{
			Id:   id,
			Text: shortErr.Error(),
		},
	}
}

func MakeResWithTraceError(id int, shortErr error, wholeErr error) ResWithError {
	return ResWithError{
		Error: Error{
			Id:    id,
			Text:  shortErr.Error(),
			Trace: wholeErr.Error(),
		},
	}
}

type Error struct {
	Id   int    `json:"id"`
	Text string `json:"text"`

	Trace string `json:"trace,omitempty"` // <--- for debug!
}
