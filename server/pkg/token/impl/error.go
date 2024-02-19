package impl

import "errors"

var (
	ErrSigningKeyIsEmpty error = errors.New("signing key is empty")
	ErrSubjectIsEmpty    error = errors.New("subject is empty")
	ErrTokenExpired      error = errors.New("token expired")
) // <--- const!
