package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/pkg/logger"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerGeoRoutes() {
	router := h.router.Group("/geo", h.parseAnyForm)
	{
		chat := router.Group("/chat")
		{
			chat.GET("/message",
				h.userIdentityByHeader, h.userNotDeleted,
				requireOffsetAndCount,
				requireLonAndLat, requireRadius,
				h.getGeoChatMessages,
			)

			chat.GET("/message/all",
				h.userIdentityByHeader, h.userNotDeleted,
				requireLonAndLat, requireRadius,
				h.getGeoChatAllMessages,
			)

			// for debug?

			chat.POST("/message",
				h.userIdentityByHeader, h.userNotDeleted,
				h.extractBodyForPostGeoChatMessage,
				h.postGeoChatMessage,
			)
		}
	}
}

// geo
// -----------------------------------------------------------------------

// GET /api/geo/chat/message
// -----------------------------------------------------------------------

func (h *Handler) getGeoChatMessages(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err) // server error!
		return
	}

	offset := ctx.GetUint64(contextOffset) // no checks
	count := ctx.GetUint64(contextCount)
	lat := ctx.GetFloat64(contextLatitude)
	lon := ctx.GetFloat64(contextLongitude)
	radius := ctx.GetUint64(contextRadius)

	// <---> services

	geoMessages, err := h.services.GetGeoChatMessages(ctx, userId,
		radius, lat, lon, offset, count)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeGeoChatMessagesResFromDomain(geoMessages)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err) // impossible error!
		return
	}

	resJsonWithOK(ctx, responseDto)
}

// GET /api/geo/chat/message/all
// -----------------------------------------------------------------------

func (h *Handler) getGeoChatAllMessages(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	lat := ctx.GetFloat64(contextLatitude)
	lon := ctx.GetFloat64(contextLongitude)
	radius := ctx.GetUint64(contextRadius)

	// <---> services

	geoMessages, err := h.services.GetGeoChatAllMessages(ctx, userId,
		radius, lat, lon)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeGeoChatMessagesResFromDomain(geoMessages)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	logger.Trace("%v", responseDto)
	resJsonWithOK(ctx, responseDto)
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

	if len(requestDto.Text) == 0 {
		resWithClientError(ctx, ec.ValidateRequestParamsFailed,
			ErrEmptyBodyParameterWithName("Text"))
		return
	}

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
		resWithErrorForClient(ctx, err)
		return
	}

	ctx.Status(http.StatusOK)
}
