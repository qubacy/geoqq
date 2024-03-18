package token

import "time"

type TokenCreator interface {
	New(Payload, time.Duration) (string, error)
}

type TokenExtractor interface {
	Parse(string) (Payload, error)
	ParseAccess(string) (Payload, error)
	Validate(string) error
}

type TokenManager interface {
	TokenExtractor
	TokenCreator
}
