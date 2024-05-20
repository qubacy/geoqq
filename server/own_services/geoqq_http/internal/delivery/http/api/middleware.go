package api

import (
	"common/pkg/logger"
	"errors"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	"strconv"
	"strings"

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

	contextLogin    = "login"
	contextPassword = "password"
)

// Header
// -----------------------------------------------------------------------

func (h *Handler) userIdentityByHeader(ctx *gin.Context) {
	accessToken, clientCode, err := extractAccessTokenAsHeader(ctx)
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

// -----------------------------------------------------------------------

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
		resWithErrorForClient(ctx, err)
		return
	}
	if wasDeleted {
		logger.Warning("request from deleted user with id %v", userId)

		resWithAuthError(ctx, ec.ValidateAccessTokenFailed, // <--- locked token!
			ErrRequestFromDeletedUser)
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
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed,
			ErrSomeParametersAreMissingWithNames([]string{"Count", "Offset"}))
		return
	}

	// extract from get-parameters

	offsetStr := ctx.Request.Form.Get(GetParameterOffset)
	offset, offsetErr := strconv.ParseUint(offsetStr, 10, 64)

	countStr := ctx.Request.Form.Get(GetParameterCount)
	count, countErr := strconv.ParseUint(countStr, 10, 64)

	if err := errors.Join(offsetErr, countErr); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
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
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed,
			ErrSomeParametersAreMissingWithNames([]string{"Lat", "Lon"}))
		return
	}

	// ***

	latStr := ctx.Request.Form.Get(GetParameterLat)
	lat, latErr := strconv.ParseFloat(latStr, 64)

	lonStr := ctx.Request.Form.Get(GetParameterLon)
	lon, lonErr := strconv.ParseFloat(lonStr, 64)

	if err := errors.Join(latErr, lonErr); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
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
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed,
			ErrSomeParametersAreMissingWithNames([]string{"Radius"}))
		return
	}

	radiusStr := ctx.Request.Form.Get(GetParameterRadius)
	radius, err := strconv.ParseUint(radiusStr, 10, 64)

	if err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
		return
	}

	// ***

	ctx.Set(contextRadius, radius)
}

// -----------------------------------------------------------------------

type routeItemId struct { // or uri params!
	Id uint64 `uri:"id" binding:"required"`
}

func requireRouteItemId(ctx *gin.Context) {
	uriParams := routeItemId{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
		return
	}

	ctx.Set(contextRouteItemId, uriParams.Id)
}

// Help
// -----------------------------------------------------------------------

func extractAccessTokenAsHeader(ctx *gin.Context) (string, int, error) {
	authValue := ctx.Request.Header.Get("Authorization")
	authParts := strings.Split(authValue, " ")

	if len(authParts) != 2 {
		return "", ec.ValidateAuthorizationHeaderFailed,
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

// -----------------------------------------------------------------------

func extractUserIdFromContext(ctx *gin.Context) (uint64, int, error) {
	_, exists := ctx.Get(contextUserId)
	if !exists {
		return 0, ec.ServerError, ErrEmptyContextParam
	}

	return ctx.GetUint64(contextUserId), ec.NoError, nil
}
