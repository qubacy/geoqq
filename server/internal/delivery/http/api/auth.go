package api

import (
	"fmt"
	"geoqq/internal/delivery/http/api/dto"
	serviceDto "geoqq/internal/service/dto"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerAuthRoutes() {
	// router := h.router.Group("/auth")

	router := h.router
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
		resWithClientError(ctx, 0, err)
		return
	}

}

func (h *Handler) postSignUp(ctx *gin.Context) {
	err := ctx.Request.ParseForm()
	if err != nil {
		resWithClientError(ctx, 0, err) // error may not be packaged!
		return
	}

	var (
		username = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // hash?
	)
	if len(username) == 0 || len(password) == 0 {
		resWithClientError(ctx, 0, ErrEmptyParameter)
		return
	}

	// ***

	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(username, password))
	if err != nil { // error may belong to different sides.
		ctx.JSON(http.StatusBadRequest,
			dto.MakeResWithError(0, fmt.Errorf("TODO")))
	}

	ctx.JSON(http.StatusOK, dto.MakeSignUpPostRes(
		out.AccessToken, out.RefreshToken))
}

func (h *Handler) putSignIn(ctx *gin.Context) {

}

// -----------------------------------------------------------------------
