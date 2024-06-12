package internal

import (
	"common/pkg/token"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/clientSide"

	"github.com/gin-gonic/gin"

	ec "common/pkg/errorForClient/geoqq"
	httpHeader "common/pkg/httpHeader/geoqq"
)

const (
	contextUserId = "user-id" // and `Ws SessionStorage`
)

func userIdentityByHeader(ctx *gin.Context, tpExtractor token.TokenPayloadExtractor) {
	accessToken, clientCode, err := httpHeader.ExtractAccessTokenWithCheck(ctx)
	if err != nil {
		resWithAuthError(ctx, clientCode, err)
		return
	}

	payload, err := tpExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

// ws
// -----------------------------------------------------------------------

func (c *Client) identify(msg clientSide.Message) error {
	payload, err := c.tpExtractor.ParseAccess(msg.AccessToken)
	if err != nil {
		c.socket.WriteClose(1000, nil)
		return nil
	}

	if payload.UserId != c.UserId {
		//...
	}

	return nil
}
