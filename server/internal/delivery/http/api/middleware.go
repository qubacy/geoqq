package api

import (
	se "geoqq/pkg/errorForClient/impl"

	"github.com/gin-gonic/gin"
)

const (
	contextUserId      = "user-id"
	contextAccessToken = "access-token"
	contextRequestDto  = "dto"
)

// only there are authorization errors!
func (h *Handler) userIdentityForGetRequest(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessTokenAsGetParam(ctx)
	if err != nil {
		resWithAuthError(ctx, clientCode, err)
		return
	}

	// TODO: to func
	payload, err := h.tokenExtractor.Parse(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, se.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

// some put requests...
func (h *Handler) userIdentityForFormRequest(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessTokenAsFormParam(ctx)
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

func (h *Handler) userIdentityByContextData(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessTokenFromContext(ctx)
	if err != nil {
		resWithAuthError(ctx, clientCode, err)
		return
	}

	payload, err := h.tokenExtractor.Parse(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, se.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextAccessToken, nil) // and now it exists?
	ctx.Set(contextUserId, payload.UserId)
}

// -----------------------------------------------------------------------

func (h *Handler) parseAnyForm(ctx *gin.Context) {
	err := ctx.Request.ParseForm()
	if err != nil {
		resWithAuthError(ctx, se.ParseRequestFailed, err)
		return
	}
}

// help
// -----------------------------------------------------------------------

func extractAccessTokenAsGetParam(ctx *gin.Context) (string, int, error) {

	// as get-parameter!
	accessToken := ctx.Request.Form.Get("accessToken")
	if len(accessToken) == 0 {
		return "", se.ParseAccessTokenFailed, // ?
			ErrEmptyAccessToken
	}

	return accessToken, se.NoError, nil
}

func extractAccessTokenAsFormParam(ctx *gin.Context) (string, int, error) {

	accessToken := ctx.Request.FormValue("access-token")
	if len(accessToken) == 0 {
		return "", se.ParseAccessTokenFailed,
			ErrEmptyAccessToken
	}

	return accessToken, se.NoError, nil
}

// -----------------------------------------------------------------------

func extractUserIdFromContext(ctx *gin.Context) (uint64, int, error) {
	_, exists := ctx.Get(contextUserId)
	if !exists {
		return 0, se.ServerError, ErrEmptyContextParam
	}

	return ctx.GetUint64(contextUserId), se.NoError, nil
}

func extractAccessTokenFromContext(ctx *gin.Context) (string, int, error) {
	_, exists := ctx.Get(contextAccessToken)
	if !exists {
		return "", se.ServerError, ErrEmptyContextParam
	}

	return ctx.GetString(contextAccessToken), se.NoError, nil
}
