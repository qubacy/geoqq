package impl

import "errors"

var (
	ErrSigningKeyIsEmpty           = errors.New("signing key is empty")
	ErrSubjectIsEmpty              = errors.New("subject is empty")
	ErrTokenExpired                = errors.New("token expired")
	ErrTokenExpNil                 = errors.New("token exp nil")
	ErrTokenIsNotPurposedForAccess = errors.New("token is not purposed for access")
) // <--- const!
