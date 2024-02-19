package impl

import (
	"geoqq/pkg/token"
	"time"

	"github.com/cristalhq/jwt/v5"
)

// without nesting ---> {"exp":4858296639,"user-id":123}
type userClaims struct {
	jwt.RegisteredClaims
	token.Payload
}

func (s *userClaims) validate() error {
	if !s.IsValidAt(time.Now()) {
		return ErrTokenExpired
	}
	return nil
}
