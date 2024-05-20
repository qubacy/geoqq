package api

import (
	"common/pkg/logger"
	"geoqq_http/internal/delivery/http/api/dto"
	"geoqq_http/internal/domain/table"
	ec "geoqq_http/internal/pkg/errorForClient/impl"
	"strconv"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerMateRoutes() {
	router := h.router.Group("/mate", h.parseAnyForm)
	{
		// TODO: what is the priority of these routes?
		chat := router.Group("/chat")
		{
			chat.GET("",
				h.userIdentityByHeader, h.userNotDeleted,
				requireOffsetAndCount, h.getMateChats,
			)
			chat.GET("/:id",
				h.userIdentityByHeader, h.userNotDeleted,
				requireRouteItemId, h.getMateChat,
			)

			chat.DELETE("/:id",
				h.userIdentityByHeader, h.userNotDeleted,
				requireRouteItemId, h.deleteMateChat,
			)
			chat.GET("/:id/message",
				h.userIdentityByHeader, h.userNotDeleted,
				requireRouteItemId, requireOffsetAndCount,
				h.getMateChatMessages,
			) // maybe group?

			// for debug?

			chat.POST("/:id/message",
				h.userIdentityByHeader, h.userNotDeleted,
				h.extractBodyForPostMateChatMessage,
				h.postMateChatMessage,
			)
		}

		request := router.Group("/request")
		{
			request.GET("",
				h.userIdentityByHeader, h.userNotDeleted,
				requireOffsetAndCount,
				h.getMateRequests,
			)
			request.GET("/count",
				h.userIdentityByHeader, h.userNotDeleted,
				h.getMateRequestCount,
			)

			// ***

			request.POST("",
				h.userIdentityByHeader, h.userNotDeleted,
				h.extractBodyForPostMateRequest,
				h.postMateRequest,
			)

			request.PUT("/:id",
				h.userIdentityByHeader, h.userNotDeleted,
				h.putMateRequest,
			)
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
		resWithErrorForClient(ctx, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.NewMateChatsResFromOutput(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	resJsonWithOK(ctx, responseDto)
}

// GET /api/mate/chat/{id}
// -----------------------------------------------------------------------

func (h *Handler) getMateChat(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)
	chatId := ctx.GetUint64(contextRouteItemId)

	// <---> services

	outputMateChat, err := h.services.GetMateChat(ctx, chatId, userId)
	if err != nil {
		resWithErrorForClient(ctx, err) // carefully!
		return
	}

	// ---> delivery

	responseDto, err := dto.NewMateChatFromOutput(outputMateChat)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	resJsonWithOK(ctx, responseDto)
}

// DELETE /api/mate/chat/{id}
// -----------------------------------------------------------------------

func (h *Handler) deleteMateChat(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)
	mateChatId := ctx.GetUint64(contextRouteItemId)

	err := h.services.DeleteMateChatForUser(ctx, mateChatId, userId)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	resWithOK(ctx)
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
		resWithErrorForClient(ctx, err)
		return
	}

	// to delivery!

	responseDto, err := dto.NewMateChatMessagesResFromDomain(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	resJsonWithOK(ctx, responseDto)
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

	if len(requestDto.Text) == 0 {
		resWithClientError(ctx, ec.ValidateRequestParamsFailed,
			ErrEmptyBodyParameterWithName("Text"))
		return
	}

	ctx.Set(contextRequestDto, &requestDto)
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
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err) // uri
		return
	}

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(*dto.MateChatMessagePostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedTypeContextParam)
		return
	}

	// to services

	mateChatId := uriParams.Id
	err = h.services.AddMessageToMateChat(ctx,
		userId, mateChatId, requestDto.Text)

	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	resWithOK(ctx)
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

	logger.Debug("count: %v", count)
	logger.Debug("offset: %v", offset)

	output, err := h.services.GetIncomingMateRequestsForUser(ctx,
		userId, offset, count)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// to delivery!

	responseDto, err := dto.NewRequestsResFromOutput(output)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	resJsonWithOK(ctx, responseDto)
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
		resWithErrorForClient(ctx, err)
		return
	}

	responseDto := dto.MateRequestCountRes{
		Count: float64(count),
	}

	resJsonWithOK(ctx, responseDto)
}

// POST /api/mate/request
// -----------------------------------------------------------------------

func (h *Handler) extractBodyForPostMateRequest(ctx *gin.Context) {
	requestDto := dto.MateRequestPostReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}
	ctx.Set(contextRequestDto, &requestDto)
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
	requestDto, converted := anyRequestDto.(*dto.MateRequestPostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedTypeContextParam)
		return
	}

	// to services

	err = h.services.AddMateRequest(ctx, userId, uint64(requestDto.UserId))
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	resWithOK(ctx)
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
		resWithServerErr(ctx, ec.ParseRequestQueryParamsFailed, err) // x-www-form-urlencoded
		return
	}

	uriParams := uriParamsPutMateRequest{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err) // uri
		return
	}

	// to services

	mateRequestId := uriParams.Id
	err = h.services.SetResultForMateRequest(ctx, userId, mateRequestId,
		table.MakeMateResultFromBool(accepted))

	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	resWithOK(ctx)
}
