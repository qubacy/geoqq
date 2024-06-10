package token

import "time"

type TokenCreator interface {
	New(Payload, time.Duration) (string, error)
}

type TokenPayloadExtractor interface {
	Parse(string) (Payload, error)
	ParseAccess(string) (Payload, error)
	ParseRefresh(string) (Payload, error)

	Validate(string) error
}

type TokenManager interface {
	TokenPayloadExtractor
	TokenCreator
}
