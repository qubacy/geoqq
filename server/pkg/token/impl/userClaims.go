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

func (self *userClaims) validate() error {
	if !self.IsValidAt(time.Now()) {
		return token.ErrTokenExpired
	}
	return nil
}
