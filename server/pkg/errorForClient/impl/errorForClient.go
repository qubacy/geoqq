package impl

import (
	"errors"
	"fmt"
)

type ErrorForClient struct {
	err error // with text!

	guiltySide int // <--- impact on http status
	clientCode int
}

func NewErrorForClient(err error, guiltySide, clientCode int) *ErrorForClient {
	return &ErrorForClient{
		err:        err,
		guiltySide: guiltySide,
		clientCode: clientCode,
	}
}

func New(err error, guiltySide, clientCode int) error {
	return NewErrorForClient(
		err,
		guiltySide,
		clientCode,
	)
}

// unwrap
// -----------------------------------------------------------------------

func (s *ErrorForClient) Unwrap() error {
	return s.err
}

func (s *ErrorForClient) UnwrapToLast() error {
	err := s.err
	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	return err
}

// read methods
// -----------------------------------------------------------------------

func (s *ErrorForClient) GuiltySide() int {
	return s.guiltySide
}

func (s *ErrorForClient) ClientCode() int {
	return s.clientCode
}

func (s *ErrorForClient) Error() string {
	return fmt.Sprintf("%v\n guilty side: %v,\n client code: %v",
		s.err, s.guiltySide, s.clientCode)
}

// unwind errors
// -----------------------------------------------------------------------

func UnwrapErrorsToLastForClient(err error) *ErrorForClient {
	var lastErrorForClient *ErrorForClient = nil
	for err != nil {
		possible, converted := err.(*ErrorForClient)
		if converted {
			lastErrorForClient = possible
		}

		err = errors.Unwrap(err)
	}
	return lastErrorForClient
}

func UnwrapErrorsToLastSideAndCode(err error) (int, int) {
	errorForClient := UnwrapErrorsToLastForClient(err)
	if errorForClient == nil {
		return Server, ServerError
	}

	return errorForClient.guiltySide,
		errorForClient.clientCode
}
