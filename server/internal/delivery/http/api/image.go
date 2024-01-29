package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image", h.userIdentity)
	{
		router.GET("/:id", h.getImage)
		router.GET("", h.getSomeImages)
	}
}

// image
// -----------------------------------------------------------------------

func (h *Handler) getImage(ctx *gin.Context) {
	_, clientCode, err := extractUserId(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// TODO:
}

func (h *Handler) getSomeImages(ctx *gin.Context) {

}
