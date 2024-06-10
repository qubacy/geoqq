package internal

import (
	"common/pkg/logger"
	"common/pkg/token"

	"github.com/gin-gonic/gin"

	ec "common/pkg/errorForClient/geoqq"
	httpEr "common/pkg/httpErrorResponse"
	httpHeader "common/pkg/httpHeader/geoqq"
)

const (
	contextUserId = "user-id" // and `Ws SessionStorage`
)

func userIdentityByHeader(ctx *gin.Context, tpExtractor token.TokenPayloadExtractor) {
	accessToken, clientCode, err := httpHeader.ExtractAccessTokenWithCheck(ctx)
	if err != nil {
		logger.Warning("%v", err) // !
		httpEr.ResWithAuthError(ctx, clientCode, err)
		return
	}

	payload, err := tpExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		logger.Warning("%v", err)
		httpEr.ResWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
		return
	}
	ctx.Set(contextUserId, payload.UserId)
}
