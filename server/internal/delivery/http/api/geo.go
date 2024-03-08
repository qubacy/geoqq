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

/*
Desc:

## GET /api/geo/chat/message

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- radius=`"<int>"`
- lon=`"<real>"`
- lat=`"<real>"`
- offset=`"<int>"`
- count=`"<int>"`

### Responses
- *200*
```json

	{
	    "messages":
	    [
	        {
	            "id": "<id>",
	            "user-id": "<id>",
	            "text": "<string>",
	            "time": "<int>"
	        },
	        ...
	    ]
	}

```
*/
func (h *Handler) getGeoChatMessages(ctx *gin.Context) {

}

// GET /api/geo/chat/message/all
// -----------------------------------------------------------------------

/*
Desc:

## GET /api/geo/chat/message/all

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- radius=`"<int>"`
- lon=`"<real>"`
- lat=`"<real>"`

### Responses
- *200*
```json

	{
	    "messages":
	    [
	        {
	            "id": "<id>",
	            "user-id": "<id>",
	            "text": "<string>",
	            "time": "<int>"
	        },
	        ...
	    ]
	}

```
*/
func (h *Handler) getGeoChatAllMessages(ctx *gin.Context) {

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
