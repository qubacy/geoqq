package dto

type ResWithError struct {
	Error `json:"error"`
}

type Error struct {
	Id int `json:"id"`
}
