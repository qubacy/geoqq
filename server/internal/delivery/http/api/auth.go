package api

import (
	"geoqq/internal/delivery/http/api/dto"
	"net/http"

	"github.com/gin-gonic/gin"
)

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
	reqDto := dto.SignInPostReq{}
	err := ctx.ShouldBindJSON(&reqDto)
	if err != nil {
		ctx.JSON(http.StatusBadRequest,
			dto.MakeResWithError(0, err))
	}

}

func (h *Handler) postSignUp(ctx *gin.Context) {

}

func (h *Handler) putSignIn(ctx *gin.Context) {

}
