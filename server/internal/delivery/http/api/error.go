package api

import (
	"errors"
	"fmt"
)

var (
	ErrEmptyRequestParameter      = errors.New("empty request parameter") // uri, x-www-form-urlencoded
	ErrEmptyBodyParameter         = errors.New("empty body parameter")    // body
	ErrEmptyAccessToken           = errors.New("empty access token")
	ErrInvalidAuthorizationHeader = errors.New("invalid authorization header")

	ErrEmptyContextParam      = errors.New("empty context param")
	ErrUnexpectedContextParam = errors.New("unexpected context param")

	ErrRequestFromDeletedUser = errors.New("request from deleted user")
)

// -----------------------------------------------------------------------

func ErrSomeParametersAreMissingWithNames(names []string) error {
	return fmt.Errorf("some parameters are missing `%v`", names)
}

func ErrEmptyRequestParameterWithName(name string) error {
	return fmt.Errorf("empty request parameter `%v`", name)
}

func ErrEmptyBodyParameterWithName(name string) error {
	return fmt.Errorf("empty body parameter `%v`", name)
}
