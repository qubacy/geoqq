package impl

import (
	"geoqq/pkg/token"
	"geoqq/pkg/utility"
	"time"

	"github.com/cristalhq/jwt/v5"
)

// without nesting ---> {"exp":4864353886,"user-id":123,"purpose":1}
type userClaims struct {
	jwt.RegisteredClaims
	token.Payload
}

func (s *userClaims) validate() error {

	if s.ExpiresAt == nil {
		return ErrTokenExpNil
	}
	if !s.IsValidAt(time.Now()) {
		return ErrTokenExpired
	}

	if err := s.Payload.Validate(); err != nil {
		return utility.NewFuncError(s.validate, err)
	}

	return nil
}
