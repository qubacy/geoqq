package api

import "errors"

var (
	ErrEmptyRequestParameter = errors.New("Empty request parameter")
	ErrEmptyBodyParameter    = errors.New("Empty body parameter") // only body

	ErrEmptyContextParam      = errors.New("Empty context param")
	ErrUnexpectedContextParam = errors.New("Unexpected context param")
	ErrEmptyAccessToken       = errors.New("Empty access token")
)
