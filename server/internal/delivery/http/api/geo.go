package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerGeoRoutes() {
	router := h.router.Group("/geo", h.parseAnyForm)
	{
		chat := router.Group("/chat")
		{
			chat.GET("/message", h.userIdentityForGetRequest,
				requireOffsetAndCount,
				requireLonAndLat,
				requireRadius,
				h.getGeoChatMessages)

			chat.GET("/message/all", h.userIdentityForGetRequest,
				requireLonAndLat,
				requireRadius,
				h.getGeoChatAllMessages)

			// for debug?

			chat.POST("/message", h.extractBodyForPostGeoChatMessage,
				h.userIdentityByContextData, h.postGeoChatMessage)
		}
	}
}

// geo
// -----------------------------------------------------------------------

// GET /api/geo/chat/message
// -----------------------------------------------------------------------

func (h *Handler) getGeoChatMessages(ctx *gin.Context) {
	_, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	offset := ctx.GetUint64(contextOffset) // no checks
	count := ctx.GetUint64(contextCount)
	lat := ctx.GetFloat64(contextLatitude)
	lon := ctx.GetFloat64(contextLongitude)
	radius := ctx.GetUint64(contextRadius)

	// <---> services

	geoMessages, err := h.services.GetGeoChatMessages(ctx,
		radius, lat, lon, offset, count)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeGeoChatMessagesResFromDomain(geoMessages)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}
	ctx.JSON(http.StatusOK, responseDto)
}

// GET /api/geo/chat/message/all
// -----------------------------------------------------------------------

func (h *Handler) getGeoChatAllMessages(ctx *gin.Context) {
	_, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	lat := ctx.GetFloat64(contextLatitude)
	lon := ctx.GetFloat64(contextLongitude)
	radius := ctx.GetUint64(contextRadius)

	// <---> services

	geoMessages, err := h.services.GetGeoChatAllMessages(ctx,
		radius, lat, lon)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeGeoChatMessagesResFromDomain(geoMessages)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}
	ctx.JSON(http.StatusOK, responseDto)
}

// POST /api/geo/chat/message
// -----------------------------------------------------------------------

/*
Desc:

## POST /api/geo/chat/message

### Request body
```json

	{
	    "access-token": "<jwt-string>",
	    "text": "<string>",
		"longitude": "<real>",
		"latitude": "<real>"
	}

```

### Responses
- *200*
*/

func (h *Handler) extractBodyForPostGeoChatMessage(ctx *gin.Context) {
	requestDto := dto.GeoChatMessagePostReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	if len(requestDto.AccessToken) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}
	if len(requestDto.Text) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}

	ctx.Set(contextAccessToken, requestDto.AccessToken)
	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) postGeoChatMessage(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// from delivery

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.GeoChatMessagePostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// to services

	err = h.services.AddMessageToGeoChat(ctx,
		userId, requestDto.Text,
		requestDto.Longitude,
		requestDto.Latitude,
	)

	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}
