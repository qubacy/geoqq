package api

import (
	"geoqq/internal/delivery/http/api/dto"
	serviceDto "geoqq/internal/service/dto"
	se "geoqq/pkg/errorForClient/impl"
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
	username, password, err := extractLoginAndPassword(ctx)
	if err != nil {
		resWithClientError(ctx, 0, ErrEmptyParameter)
		return
	}

	// ***

	out, err := h.services.SignIn(ctx,
		serviceDto.MakeSignInInp(username, password))

	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, dto.MakeSignInPostRes(
		out.AccessToken, out.RefreshToken))
}

func (h *Handler) postSignUp(ctx *gin.Context) {
	username, password, err := extractLoginAndPassword(ctx)
	if err != nil {
		resWithClientError(ctx, 0, ErrEmptyParameter)
		return
	}
	// ***

	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(username, password))

	if err != nil { // error may belong to different sides!
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, dto.MakeSignUpPostRes(
		out.AccessToken, out.RefreshToken))
}

func (h *Handler) putSignIn(ctx *gin.Context) {

}

// private
// -----------------------------------------------------------------------

func extractLoginAndPassword(ctx *gin.Context) (string, string, error) {
	err := ctx.Request.ParseForm()
	if err != nil {
		return "", "", err
	}

	var (
		username = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // hash?
	)
	if len(username) == 0 || len(password) == 0 {
		return "", "", ErrEmptyParameter
	}

	return username, password, nil
}
