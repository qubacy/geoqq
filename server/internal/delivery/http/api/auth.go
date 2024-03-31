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
		router.POST("/sign-in", h.parseAnyForm,
			extractLoginAndPassword,
			h.postSignIn)

		router.POST("/sign-up", h.parseAnyForm,
			extractLoginAndPassword,
			h.postSignUp)

		router.PUT("/sign-in", h.parseAnyForm,
			extractRefreshToken,
			h.putSignIn)
	}
}

// auth
// -----------------------------------------------------------------------

func (h *Handler) postSignIn(ctx *gin.Context) {

	username := ctx.GetString(contextUsername)
	passwordHashInBase64 := ctx.GetString(contextPassword)

	out, err := h.services.SignIn(ctx,
		serviceDto.MakeSignInInp(username, passwordHashInBase64))

	if err != nil { // error may belong to different sides!

		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignInPostRes(
		out.AccessToken, out.RefreshToken))
}

func (h *Handler) postSignUp(ctx *gin.Context) {

	username := ctx.GetString(contextUsername)
	passwordHashInBase64 := ctx.GetString(contextPassword)

	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(username, passwordHashInBase64))

	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignUpPostRes(
		out.AccessToken, out.RefreshToken))
}

func (h *Handler) putSignIn(ctx *gin.Context) {
	refreshToken := ctx.GetString(contextRefreshToken)
	out, err := h.services.RefreshTokens(ctx, refreshToken)
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignUpPutRes(
		out.AccessToken, out.RefreshToken))
}

// middlewares
// -----------------------------------------------------------------------

// application/x-www-form-urlencoded

func extractLoginAndPassword(ctx *gin.Context) {
	var (
		username = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // password hash in base64!
	)
	if len(username) == 0 || len(password) == 0 {
		resWithClientError(ctx,
			se.ValidateRequestFailed,
			ErrEmptyRequestParameter,
		)
		return
	}

	ctx.Set(contextPassword, password)
	ctx.Set(contextUsername, username)
}

func extractRefreshToken(ctx *gin.Context) {
	var (
		refreshToken = ctx.Request.FormValue("refresh-token")
	)
	if len(refreshToken) == 0 {
		resWithClientError(ctx,
			se.ValidateRequestFailed,
			ErrEmptyRequestParameter,
		)
		return
	}

	ctx.Set(contextRefreshToken, refreshToken)
}
