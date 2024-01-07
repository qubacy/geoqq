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

// -----------------------------------------------------------------------

func (h *Handler) postSignIn(c *gin.Context) {

}

func (h *Handler) postSignUp(c *gin.Context) {

}

func (h *Handler) putSignIn(c *gin.Context) {

}
