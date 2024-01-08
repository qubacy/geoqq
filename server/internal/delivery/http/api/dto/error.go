package dto

type ResWithError struct {
	Error `json:"error"`
}

func MakeResWithError(id int, err error) ResWithError {
	return ResWithError{
		Error: Error{
			Id:   id,
			Text: err.Error(),
		},
	}
}

type Error struct {
	Id   int    `json:"id"`
	Text string `json:"text"`
}
