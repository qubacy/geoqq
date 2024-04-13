package api

import (
	"encoding/json"
	"fmt"
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/pkg/errorForClient/impl"
	"geoqq/pkg/utility"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image", h.parseAnyForm)
	{
		router.GET("/:id",
			h.userIdentityForGetRequest, h.userNotDeleted,
			h.getImage,
		)
		router.POST("", h.extractBodyFromPostForGetSomeImages,
			h.userIdentityByContextData, h.userNotDeleted,
			h.postForGetSomeImages,
		) // can be done better!?

		router.POST("/new", h.extractBodyFromPostNewImage,
			h.userIdentityByContextData, h.userNotDeleted,
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

// POST /api/image
// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromPostForGetSomeImages(ctx *gin.Context) {
	requestDto := dto.SomeImagesReq{}
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

func (h *Handler) postForGetSomeImages(ctx *gin.Context) {
	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.SomeImagesReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// *** to service

	fmt.Println(requestDto)

	images, err := h.services.GetImagesByIds(ctx,
		utility.ConvertSliceFloat64ToUint64(requestDto.Ids))
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// to delivery

	// may need to be converted
	// 		to a struct `SomeImagesRes`?

	ctx.JSON(http.StatusOK, images)
}

// POST /api/image/new
// -----------------------------------------------------------------------

func (h *Handler) extractBodyFromPostNewImage(ctx *gin.Context) {
	requestDto := dto.ImagePostReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// ***

	if len(requestDto.AccessToken) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}
	if len(requestDto.Image.Content) == 0 {
		resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}

	// to next handler!

	ctx.Set(contextAccessToken, requestDto.AccessToken)
	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) postNewImage(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)

	anyRequestDto, _ := ctx.Get(contextRequestDto) // always exists!
	requestDto, converted := anyRequestDto.(dto.ImagePostReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	jsonBytes, err := json.Marshal(requestDto)
	if err != nil {
		fmt.Println(err)
	}
	err = os.WriteFile("image.json", jsonBytes, 0666)
	if err != nil {
		fmt.Println(err)
	}

	imageId, err := h.services.AddImageToUser(ctx,
		userId, requestDto.ToInp())
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, dto.MakeImagePostRes(imageId))
}
