package api

import "errors"

var (
	ErrSomeParametersAreMissing = errors.New("some parameters are missing")
	ErrEmptyRequestParameter    = errors.New("empty request parameter")
	ErrEmptyBodyParameter       = errors.New("empty body parameter") // only body

	ErrEmptyContextParam      = errors.New("empty context param")
	ErrUnexpectedContextParam = errors.New("unexpected context param")
	ErrEmptyAccessToken       = errors.New("empty access token")
)
