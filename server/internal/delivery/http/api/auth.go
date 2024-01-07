package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerAuthRoutes() {
	router := h.router.Group("/auth")
	{
		router.POST("/sign-in", h.postSignIn)
		router.POST("/sign-up", h.postSignUp)
		router.PUT("/sign-in", h.putSignIn)
	}
}

// auth
// -----------------------------------------------------------------------

func (h *Handler) postSignIn(ctx *gin.Context) {

}

func (h *Handler) postSignUp(ctx *gin.Context) {

}

func (h *Handler) putSignIn(ctx *gin.Context) {

}
