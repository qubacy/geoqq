package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerUserRoutes() {
	h.router.GET("/my-profile", h.getMyProfile)
	h.router.PUT("/my-profile", h.putMyProfile)

	// ***

	router := h.router.Group("/user", h.userIdentity)
	{
		router.GET("/:id", h.getUser)
		router.GET("", h.getSomeUsers)
	}
}

// my-profile
// -----------------------------------------------------------------------

func (h *Handler) getMyProfile(ctx *gin.Context) {

}

func (h *Handler) putMyProfile(ctx *gin.Context) {

}

// user
// -----------------------------------------------------------------------

func (h *Handler) getUser(ctx *gin.Context) {

}

func (h *Handler) getSomeUsers(ctx *gin.Context) {

}

// private
// -----------------------------------------------------------------------
