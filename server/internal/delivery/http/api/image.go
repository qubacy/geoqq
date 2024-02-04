package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/pkg/errorForClient/impl"
	se "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image")
	{
		router.GET("/:id", h.parseAnyForm, h.userIdentityForGetRequest, h.getImage)
		router.GET("", h.parseAnyForm, h.extractBodyFromGetSomeImages,
			h.userIdentityByContextData, h.getSomeImages)
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

	image, err := h.services.GetImageById(ctx, uriParams.Id)
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, image)
}

// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromGetSomeImages(ctx *gin.Context) {
	requestDto := dto.ImagesReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
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

	// ***

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	requestDto, converted := anyRequestDto.(dto.ImagesReq)
	if !converted {
		resWithClientError(ctx, ec.ParseRequestParamsFailed, err)
		return
	}

	images, err := h.services.GetImagesByIds(ctx, requestDto.GetIdsAsSliceOfUint64())
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, images)
}
