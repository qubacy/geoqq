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
		router.POST("/sign-in", h.parseAnyForm, h.postSignIn)
		router.POST("/sign-up", h.parseAnyForm, h.postSignUp)
		router.PUT("/sign-in", h.parseAnyForm, h.putSignIn)
	}
}

// auth
// -----------------------------------------------------------------------

func (h *Handler) postSignIn(ctx *gin.Context) {
	// err more important!

	username, password, code, err := extractLoginAndPassword(ctx)
	if err != nil {
		// code, err can be combined?

		resWithClientError(ctx, code, err)
		return
	}

	// ***

	out, err := h.services.SignIn(ctx,
		serviceDto.MakeSignInInp(username, password))

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
	username, password, code, err := extractLoginAndPassword(ctx)
	if err != nil {
		resWithClientError(ctx, code, err)
		return
	}

	// ***

	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(username, password))

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
	refreshToken, code, err := extractRefreshToken(ctx)
	if err != nil {
		resWithClientError(ctx, code, err)
		return
	}

	out, err := h.services.RefreshTokens(ctx, refreshToken)
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK, dto.MakeSignUpPutRes(
		out.AccessToken, out.RefreshToken))
}

// private
// -----------------------------------------------------------------------

func extractLoginAndPassword(ctx *gin.Context) (
	string, string, int, error,
) {
	var (
		username = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // hash?
	)
	if len(username) == 0 || len(password) == 0 {
		return "", "", se.ValidateRequestParamsFailed, ErrEmptyRequestParameter
	}

	return username, password, se.NoError, nil
}

func extractRefreshToken(ctx *gin.Context) (
	string, int, error,
) {
	var (
		refreshToken = ctx.Request.FormValue("refresh-token")
	)
	if len(refreshToken) == 0 {
		return "", se.ValidateRequestParamsFailed, ErrEmptyRequestParameter
	}

	return refreshToken, se.NoError, nil
}
