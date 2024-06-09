package payload

type ErrorPayload struct {
	HttpCode float64   `json:"code"`
	Body     ErrorBody `json:"error"`
}

type ErrorBody struct {
	Id    float64 `json:"id"`
	Text  string  `json:"text"`
	Trace string  `json:"trace"`
}

func MakeErrorPayloadWithTrace(httpCode, id int, shortErr error, wholeErr error) ErrorPayload {
	return ErrorPayload{
		HttpCode: float64(httpCode),
		Body: ErrorBody{
			Id:    float64(id),
			Text:  shortErr.Error(),
			Trace: wholeErr.Error(),
		},
	}
}
