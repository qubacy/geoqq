package token

import "errors"

var (
	ErrSigningKeyIsEmpty error = errors.New("Signing key is empty")
	ErrSubjectIsEmpty    error = errors.New("Subject is empty")
	ErrTokenExpired      error = errors.New("Token expired")
) // <--- const!
