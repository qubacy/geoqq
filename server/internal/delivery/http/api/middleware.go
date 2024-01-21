package api

import (
	se "geoqq/pkg/errorForClient/impl"

	"github.com/gin-gonic/gin"
)

const (
	contextUserId = "user-id"
)

// only there are authorization errors!
func (h *Handler) userIdentity(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessToken(ctx)
	if err != nil {
		resWithAuthError(ctx, clientCode, err)
		return
	}

	payload, err := h.tokenExtractor.Parse(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, se.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

// help
// -----------------------------------------------------------------------

func extractAccessToken(ctx *gin.Context) (string, int, error) {
	err := ctx.Request.ParseForm()
	if err != nil {
		return "", se.ParseRequestParamsFailed, err
	}

	// get-parameter!
	accessToken := ctx.Request.Form.Get("accessToken")
	if len(accessToken) == 0 {
		return "", se.ParseAccessTokenFailed,
			ErrEmptyAccessToken
	}

	return accessToken, se.NoError, nil
}

func extractUserId(ctx *gin.Context) (uint64, int, error) {
	_, exists := ctx.Get(contextUserId)
	if !exists {
		return 0, se.ServerError, ErrEmptyContextParam
	}

	return ctx.GetUint64(contextUserId), se.NoError, nil
}
