package api

import (
	"errors"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/logger"
	"strconv"

	"github.com/gin-gonic/gin"
)

const (
	contextUserId       = "user-id"
	contextAccessToken  = "access-token"
	contextRefreshToken = "refresh-token"
	contextRequestDto   = "dto"

	contextOffset = "offset"
	contextCount  = "count"

	contextLongitude = "longitude"
	contextLatitude  = "latitude"
	contextRadius    = "radius"

	contextRouteItemId = "routeItemId"

	contextUsername = "username"
	contextPassword = "password"
)

// -----------------------------------------------------------------------

// only there are authorization errors!
func (h *Handler) userIdentityForGetRequest(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessTokenAsGetParam(ctx)
	if err != nil {
		resWithAuthError(ctx, clientCode, err)
		return
	}

	// TODO: into a separate function?
	payload, err := h.tokenExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
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

	payload, err := h.tokenExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
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

	payload, err := h.tokenExtractor.ParseAccess(accessToken) // and validate!
	if err != nil {
		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

type bodyWithAccessToken struct {
	AccessToken string `json:"access-token" binding:"required"`
}

func (h *Handler) userIdentityByBodyWithAccessToken(ctx *gin.Context) {
	requestDto := bodyWithAccessToken{} // ?
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	payload, err := h.tokenExtractor.ParseAccess(requestDto.AccessToken)
	if err != nil {
		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, err)
		return
	}

	ctx.Set(contextUserId, payload.UserId)
}

func (h *Handler) userNotDeleted(ctx *gin.Context) {

	// no checks required before extraction user id!

	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// ***

	wasDeleted, err := h.services.WasUserWithIdDeleted(ctx, userId)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}
	if wasDeleted {
		logger.Warning("request from deleted user with id %v", userId)
		resWithAuthError(ctx, ec.InvalidAccessToken, ErrRequestFromDeletedUser)
	}
}

// -----------------------------------------------------------------------

func (h *Handler) parseAnyForm(ctx *gin.Context) {
	err := ctx.Request.ParseForm()
	if err != nil {
		resWithAuthError(ctx, ec.ParseAnyFormFailed, err)
		return
	}
}

// -----------------------------------------------------------------------

const GetParameterCount = "count"
const GetParameterOffset = "offset"

func requireOffsetAndCount(ctx *gin.Context) {

	// check availability

	existsCount := ctx.Request.Form.Has(GetParameterCount)
	existsOffset := ctx.Request.Form.Has(GetParameterOffset)

	if !existsOffset || !existsCount {
		resWithClientError(ctx, ec.ParseRequestParamsFailed,
			ErrSomeParametersAreMissing)
		return
	}

	// extract from get-parameters

	offsetStr := ctx.Request.Form.Get(GetParameterOffset)
	offset, offsetErr := strconv.ParseUint(offsetStr, 10, 64)

	countStr := ctx.Request.Form.Get(GetParameterCount)
	count, countErr := strconv.ParseUint(countStr, 10, 64)

	if err := errors.Join(offsetErr, countErr); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	// save for next handlers

	ctx.Set(contextOffset, offset)
	ctx.Set(contextCount, count)
}

// -----------------------------------------------------------------------

const GetParameterLon = "lon"
const GetParameterLat = "lat"

func requireLonAndLat(ctx *gin.Context) {
	if !ctx.Request.Form.Has(GetParameterLat) ||
		!ctx.Request.Form.Has(GetParameterLon) {
		resWithClientError(ctx, ec.ParseRequestParamsFailed,
			ErrSomeParametersAreMissing)
		return
	}

	// ***

	latStr := ctx.Request.Form.Get(GetParameterLat)
	lat, latErr := strconv.ParseFloat(latStr, 64)

	lonStr := ctx.Request.Form.Get(GetParameterLon)
	lon, lonErr := strconv.ParseFloat(lonStr, 64)

	if err := errors.Join(latErr, lonErr); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	// ***

	ctx.Set(contextLongitude, lon)
	ctx.Set(contextLatitude, lat)
}

// -----------------------------------------------------------------------

const GetParameterRadius = "radius"

func requireRadius(ctx *gin.Context) {
	if !ctx.Request.Form.Has(GetParameterRadius) {
		resWithClientError(ctx, ec.ParseRequestParamsFailed,
			ErrSomeParametersAreMissing)
		return
	}

	radiusStr := ctx.Request.Form.Get(GetParameterRadius)
	radius, err := strconv.ParseUint(radiusStr, 10, 64)

	if err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	ctx.Set(contextRadius, radius)
}

// -----------------------------------------------------------------------

type routeItemId struct { // or uri params!
	Id uint64 `uri:"id" binding:"required"`
}

func requireRouteItemId(ctx *gin.Context) {
	uriParams := routeItemId{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	ctx.Set(contextRouteItemId, uriParams.Id)
}

// help
// -----------------------------------------------------------------------

func extractAccessTokenAsGetParam(ctx *gin.Context) (string, int, error) {

	// as get-parameter!
	accessToken := ctx.Request.Form.Get("accessToken")
	if len(accessToken) == 0 {
		return "", ec.ParseAccessTokenFailed, // ?
			ErrEmptyAccessToken
	}

	return accessToken, ec.NoError, nil
}

func extractAccessTokenAsFormParam(ctx *gin.Context) (string, int, error) {

	accessToken := ctx.Request.FormValue("access-token")
	if len(accessToken) == 0 {
		return "", ec.ParseAccessTokenFailed,
			ErrEmptyAccessToken
	}

	return accessToken, ec.NoError, nil
}

// -----------------------------------------------------------------------

func extractUserIdFromContext(ctx *gin.Context) (uint64, int, error) {
	_, exists := ctx.Get(contextUserId)
	if !exists {
		return 0, ec.ServerError, ErrEmptyContextParam
	}

	return ctx.GetUint64(contextUserId), ec.NoError, nil
}

func extractAccessTokenFromContext(ctx *gin.Context) (string, int, error) {
	_, exists := ctx.Get(contextAccessToken)
	if !exists {
		return "", ec.ServerError, ErrEmptyContextParam
	}

	return ctx.GetString(contextAccessToken), ec.NoError, nil
}
