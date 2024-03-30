package api

import (
	"fmt"
	"geoqq/internal/delivery/http/api/dto"
	"geoqq/internal/domain/table"
	ec "geoqq/pkg/errorForClient/impl"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerMateRoutes() {
	router := h.router.Group("/mate", h.parseAnyForm)
	{
		// TODO: what is the priority of these routes?
		chat := router.Group("/chat")
		{
			chat.GET("", h.userIdentityForGetRequest, requireOffsetAndCount, h.getMateChats)
			chat.GET("/:id", h.userIdentityForGetRequest, requireRouteItemId,
				h.getMateChat)

			chat.DELETE("/:id", h.deleteMateChat)
			chat.GET("/:id/message", h.userIdentityForGetRequest,
				requireRouteItemId, requireOffsetAndCount,
				h.getMateChatMessages) // maybe group?

			// for debug?

			chat.POST("/:id/message", h.extractBodyForPostMateChatMessage,
				h.userIdentityByContextData, h.postMateChatMessage)
		}

		request := router.Group("/request")
		{
			request.GET("", h.userIdentityForGetRequest,
				requireOffsetAndCount, h.getMateRequests)
			request.GET("/count", h.userIdentityForGetRequest, h.getMateRequestCount)

			// ***

			request.POST("", h.extractBodyForPostMateRequest,
				h.userIdentityByContextData, h.postMateRequest)

			request.PUT("/:id", h.userIdentityForFormRequest, h.putMateRequest)
		}
	}
}

// chat
// -----------------------------------------------------------------------

// GET /api/mate/chat
// -----------------------------------------------------------------------

func (h *Handler) getMateChats(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}
	offset := ctx.GetUint64(contextOffset)
	count := ctx.GetUint64(contextCount)

	// <---> services

	output, err := h.services.GetMateChatsForUser(ctx,
		userId, offset, count)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeMateChatsResFromOutput(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}
	ctx.JSON(http.StatusOK, responseDto)
}

// GET /api/mate/chat/{id}
// -----------------------------------------------------------------------

func (h *Handler) getMateChat(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}
	chatId := ctx.GetUint64(contextRouteItemId)

	// ***

	fmt.Println(userId)
	fmt.Println(chatId)
}

// DELETE /api/mate/chat/{id}
// -----------------------------------------------------------------------

func (h *Handler) deleteMateChat(ctx *gin.Context) {

}

// GET /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

func (h *Handler) getMateChatMessages(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}
	offset := ctx.GetUint64(contextOffset)
	count := ctx.GetUint64(contextCount)
	chatId := ctx.GetUint64(contextRouteItemId)

	// to services!

	output, err := h.services.ReadMateChatMessagesByChatId(ctx,
		userId, chatId, offset, count)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// to delivery!

	responseDto, err := dto.MakeMateChatMessagesResFromDomain(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}
	ctx.JSON(http.StatusOK, responseDto)
}

// POST /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

/*
Desc:

## POST /api/mate/chat/{id}/message

### Request body
```json

	{
	    "access-token": "<jwt-string>",
	    "text": "<string>"
	}

```

### Responses
- *200*
*/

func (h *Handler) extractBodyForPostMateChatMessage(ctx *gin.Context) {
	requestDto := dto.MateChatMessagePostReq{}
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
	ctx.Set(contextRequestDto, requestDto) // all body with access token.
}

type uriParamsPostMateChatMessage struct {
	Id uint64 `uri:"id" binding:"required"`
}

func (h *Handler) postMateChatMessage(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// from delivery

	uriParams := uriParamsPostMateChatMessage{} // or move to previous middleware?
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err) // uri
		return
	}

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.MateChatMessagePostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// to services

	mateChatId := uriParams.Id
	err = h.services.AddMessageToMateChat(ctx,
		userId, mateChatId, requestDto.Text)

	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}

// mate-request
// -----------------------------------------------------------------------

// GET /api/mate/request
// -----------------------------------------------------------------------

func (h *Handler) getMateRequests(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}
	offset := ctx.GetUint64(contextOffset) // no checks
	count := ctx.GetUint64(contextCount)

	// to-from services

	output, err := h.services.GetIncomingMateRequestsForUser(ctx,
		userId, offset, count)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// to delivery!

	responseDto, err := dto.MakeRequestsResFromOutput(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	ctx.JSON(http.StatusOK, responseDto)
}

// GET /api/mate/request/count
// -----------------------------------------------------------------------

func (h *Handler) getMateRequestCount(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx) // current!
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	count, err := h.services.GetIncomingMateRequestCountForUser(ctx, userId)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	responseDto := dto.MateRequestCountRes{
		Count: float64(count),
	}

	ctx.JSON(http.StatusOK, responseDto)
}

// POST /api/mate/request
// -----------------------------------------------------------------------

func (h *Handler) extractBodyForPostMateRequest(ctx *gin.Context) {
	requestDto := dto.MateRequestPostReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// simple request validation...

	if len(requestDto.AccessToken) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}

	ctx.Set(contextAccessToken, requestDto.AccessToken)
	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) postMateRequest(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx) // current!
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// from delivery

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	ctx.Set(contextRequestDto, nil)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.MateRequestPostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// to services

	err = h.services.AddMateRequest(ctx, userId, uint64(requestDto.UserId))
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}

// PUT /api/mate/request/{id}
// -----------------------------------------------------------------------

type uriParamsPutMateRequest struct {
	Id uint64 `uri:"id" binding:"required"` // ?
}

func (h *Handler) putMateRequest(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx) // current!
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	acceptedStr := ctx.Request.FormValue("accepted")
	accepted, err := strconv.ParseBool(acceptedStr)
	if err != nil {
		resWithServerErr(ctx, ec.ParseRequestParamsFailed, err) // x-www-form-urlencoded
		return
	}

	uriParams := uriParamsPutMateRequest{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err) // uri
		return
	}

	// to services

	mateRequestId := uriParams.Id
	err = h.services.SetResultForMateRequest(ctx, userId, mateRequestId,
		table.MakeMateResultFromBool(accepted))
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}
