package dto

type ResWithError struct {
	Error `json:"error"`
}

func MakeResWithError(id int, shortErr error) ResWithError {
	return ResWithError{
		Error: Error{
			Id:   float64(id),
			Text: shortErr.Error(),
		},
	}
}

func MakeResWithTraceError(id int, shortErr error, wholeErr error) ResWithError {
	return ResWithError{
		Error: Error{
			Id:    float64(id),
			Text:  shortErr.Error(),
			Trace: wholeErr.Error(),
		},
	}
}

type Error struct {
	Id   float64 `json:"id"` // TODO: convert to float64
	Text string  `json:"text"`

	Trace string `json:"trace,omitempty"` // <--- for debug!
}
