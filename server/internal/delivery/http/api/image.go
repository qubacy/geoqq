package api

import (
	ec "geoqq/pkg/errorForClient/impl"
	se "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image", h.userIdentity)
	{
		router.GET("/:id", h.getImage)
		router.GET("", h.getSomeImages)
	}
}

// image
// -----------------------------------------------------------------------

type uriParamsGetImage struct {
	Id uint64 `uri:"id" binding:"required"` // ?
}

func (h *Handler) getImage(ctx *gin.Context) {
	_, clientCode, err := extractUserId(ctx)
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

func (h *Handler) getSomeImages(ctx *gin.Context) {

}
