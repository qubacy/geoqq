package internal

import (
	"common/pkg/token"
	"common/pkg/utility"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide"

	"github.com/gin-gonic/gin"

	ec "common/pkg/errorForClient/geoqq"
	httpHeader "common/pkg/httpHeader/geoqq"
)

const (
	contextUserId = "user-id" // ctx and `Ws SessionStorage`
)

func userIdentityByHeader(ctx *gin.Context, tpExtractor token.TokenPayloadExtractor) {
	accessToken, clientCode, err := httpHeader.ExtractAccessTokenWithCheck(ctx)
	if err != nil {
		httpResWithAuthError(ctx, clientCode, err)
		return
	}

	payload, err := tpExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		httpResWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

// ws
// -----------------------------------------------------------------------

func (c *Client) assertUserIdentity(msg clientSide.Message) error {
	payload, err := c.tpExtractor.ParseAccess(msg.AccessToken)
	if err != nil {
		return utility.NewFuncError(c.assertUserIdentity, err)
	}

	if payload.UserId != c.userId {

	}

	return nil
}
