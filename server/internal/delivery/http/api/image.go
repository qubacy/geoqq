package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/pkg/utility"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image", h.parseAnyForm)
	{
		router.GET("/:id",
			h.userIdentityByHeader, h.userNotDeleted,
			h.getImage,
		)
		router.POST("",
			h.userIdentityByHeader, h.userNotDeleted,
			h.extractBodyFromPostForGetSomeImages,
			h.postForGetSomeImages,
		) // can be done better!?

		router.POST("/new",
			h.userIdentityByHeader, h.userNotDeleted,
			h.extractBodyFromPostNewImage,
			h.postNewImage,
		)
	}
}

// image
// -----------------------------------------------------------------------

// GET /api/image/{id}
// -----------------------------------------------------------------------

// or move to dto?
type uriParamsGetImage struct {
	Id uint64 `uri:"id" binding:"required"` // ?
}

func (h *Handler) getImage(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)
	uriParams := uriParamsGetImage{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
		return
	}

	// ***

	image, err := h.services.GetImageById(ctx, userId, uriParams.Id)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// TODO: does it need to be converted?

	resJsonWithOK(ctx, image)
}

// POST /api/image
// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromPostForGetSomeImages(ctx *gin.Context) {
	requestDto := dto.SomeImagesReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	if len(requestDto.Ids) == 0 {
		resWithClientError(ctx, ec.ValidateRequestParamsFailed,
			ErrEmptyBodyParameterWithName("Ids"))
		return
	}

	ctx.Set(contextRequestDto, &requestDto)
}

func (h *Handler) postForGetSomeImages(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)
	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(*dto.SomeImagesReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// *** to service

	images, err := h.services.GetImagesByIds(ctx, userId,
		utility.ConvertSliceFloat64ToUint64(requestDto.Ids))
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// to delivery

	// may need to be converted
	// 		to a struct `SomeImagesRes`?

	resJsonWithOK(ctx, images)
}

// POST /api/image/new
// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromPostNewImage(ctx *gin.Context) {
	requestDto := dto.ImagePostReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	if len(requestDto.Image.Content) == 0 {
		resWithClientError(ctx, ec.ValidateRequestParamsFailed,
			ErrEmptyBodyParameterWithName("Image.Content"))
		return
	}

	ctx.Set(contextRequestDto, &requestDto)
}

func (h *Handler) postNewImage(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)

	anyRequestDto, _ := ctx.Get(contextRequestDto) // always exists!
	requestDto, converted := anyRequestDto.(*dto.ImagePostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// ***

	imageId, err := h.services.AddImageToUser(ctx,
		userId, requestDto.ToDynamicInp())
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	responseDto := dto.MakeImagePostRes(imageId) // one number.
	resJsonWithOK(ctx, responseDto)
}
