package api

import (
	"errors"
	"fmt"
)

var (
	ErrEmptyRequestParameter = errors.New("empty request parameter") // uri, x-www-form-urlencoded
	ErrEmptyBodyParameter    = errors.New("empty body parameter")    // body

	ErrInvalidAuthorizationHeader = errors.New("invalid authorization header")
	ErrEmptyAccessToken           = errors.New("empty access token")

	ErrEmptyContextParam          = errors.New("empty context param")
	ErrUnexpectedTypeContextParam = errors.New("unexpected type context param")

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
