package geoqq

import (
	"errors"
	"strings"

	ec "common/pkg/errorForClient/geoqq"

	"github.com/gin-gonic/gin"
)

var (
	ErrInvalidAuthorizationHeader = errors.New("invalid authorization header")
	ErrEmptyAccessToken           = errors.New("empty access token")
)

func ExtractAccessTokenWithCheck(ctx *gin.Context) (string, int, error) {
	authValue := ctx.Request.Header.Get("Authorization")
	authParts := strings.Split(authValue, " ")

	// ***

	if len(authParts) != 2 {
		return "", ec.ValidateAuthorizationHeaderFailed, // parse error!
			ErrInvalidAuthorizationHeader
	}
	if authParts[0] != "Bearer" {
		return "", ec.ValidateAuthorizationHeaderFailed,
			ErrInvalidAuthorizationHeader
	}

	accessToken := authParts[1]
	if len(accessToken) == 0 {
		return "", ec.ValidateRequestAccessTokenFailed,
			ErrEmptyAccessToken
	}

	return accessToken, ec.NoError, nil
}
