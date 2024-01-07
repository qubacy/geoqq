package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerImageRoutes() {
	router := h.router.Group("/image")
	{
		router.GET("/:id", h.getImage)
		router.GET("", h.getSomeImages)
	}
}

// image
// -----------------------------------------------------------------------

func (h *Handler) getImage(ctx *gin.Context) {

}

func (h *Handler) getSomeImages(ctx *gin.Context) {

}
