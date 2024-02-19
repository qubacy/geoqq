package api

import (
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
			chat.GET("", h.userIdentityForGetRequest, h.getMateChats)
			chat.DELETE("/:id", h.deleteMateChat)
			chat.GET("/:id/message", h.userIdentityForGetRequest, h.getMateChatMessages) // maybe group?
		}

		request := router.Group("/request")
		{
			request.GET("", h.userIdentityForGetRequest, h.getMateRequests)
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

func (h *Handler) getMateChats(ctx *gin.Context) {

}

func (h *Handler) deleteMateChat(ctx *gin.Context) {

}

func (h *Handler) getMateChatMessages(ctx *gin.Context) {

}

// request
// -----------------------------------------------------------------------

func (h *Handler) getMateRequests(ctx *gin.Context) {

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
