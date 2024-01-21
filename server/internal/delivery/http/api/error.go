package api

import "errors"

var (
	ErrEmptyRequestParameter = errors.New("Empty parameter")
	ErrEmptyContextParam     = errors.New("Empty context param")
	ErrEmptyAccessToken      = errors.New("Empty access token")
)
