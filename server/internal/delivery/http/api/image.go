package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image", h.parseAnyForm)
	{
		router.GET("/:id", h.userIdentityForGetRequest, h.getImage)
		router.GET("", h.extractBodyFromGetSomeImages,
			h.userIdentityByContextData, h.getSomeImages) // can be done better!
	}
}

// image
// -----------------------------------------------------------------------

// or move to dto?
type uriParamsGetImage struct {
	Id uint64 `uri:"id" binding:"required"` // ?
}

func (h *Handler) getImage(ctx *gin.Context) {
	_, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// ***

	uriParams := uriParamsGetImage{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	// ***

	image, err := h.services.GetImageById(ctx, uriParams.Id)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, image)
}

// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromGetSomeImages(ctx *gin.Context) {
	requestDto := dto.ImagesReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// ***

	if len(requestDto.AccessToken) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}
	if len(requestDto.Ids) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}

	// ***

	ctx.Set(contextAccessToken, requestDto.AccessToken)
	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) getSomeImages(ctx *gin.Context) {
	_, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// *** handler level checks

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.ImagesReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// *** to service

	images, err := h.services.GetImagesByIds(ctx, requestDto.GetIdsAsSliceOfUint64())
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, images)
}
