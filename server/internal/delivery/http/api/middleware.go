package api

import "github.com/gin-gonic/gin"

const ()

// TODO: заголовок или get параметр?

func (h *Handler) userIdentity(ctx *gin.Context) {
	accessToken, err := extractAccessToken(ctx)
	if err != nil {
		resWithClientError(ctx, 0, err)
		return
	}

	payload, err := h.tokenExtractor.Parse(accessToken)
	if err != nil {
		resWithClientError(ctx, 0, err)
		return
	}

	ctx.Set("user-id", payload.UserId)
}

// private
// -----------------------------------------------------------------------

func extractAccessToken(ctx *gin.Context) (string, error) {
	err := ctx.Request.ParseForm()
	if err != nil {
		return "", err
	}

	accessToken := ctx.Request.Form.Get("accessToken")
	if len(accessToken) == 0 {
		return "", ErrEmptyParameter
	}

	return accessToken, nil
}
