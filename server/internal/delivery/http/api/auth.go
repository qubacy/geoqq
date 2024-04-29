package api

import (
	"geoqq/internal/delivery/http/api/dto"
	se "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/internal/service"
	serviceDto "geoqq/internal/service/dto"
	"geoqq/pkg/logger"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerAuthRoutes() {

	// ?
	// router := h.router.Group("/auth")
	//
	// ---> /api/auth/sign-in, ...

	router := h.router
	{
		router.POST("/sign-in", h.parseAnyForm,
			extractLoginAndPassword,
			h.postSignIn,
		)

		router.POST("/sign-up", h.parseAnyForm,
			extractLoginAndPassword,
			h.postSignUp,
		)

		router.PUT("/sign-in", h.parseAnyForm,
			extractRefreshToken,
			h.putSignIn,
		)
	}
}

// auth
// -----------------------------------------------------------------------

// POST /api/sign-in
// -----------------------------------------------------------------------

func (h *Handler) postSignIn(ctx *gin.Context) {
	username := ctx.GetString(contextUsername)
	passwordHash := ctx.GetString(contextPassword)

	// ***

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.SignIn(ctx,
		serviceDto.MakeSignInInp(username, passwordHash))

	if err != nil { // error may belong to different sides!
		logger.Warning("%v", err)
		resWithErrorForClient(ctx, err)
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignInPostRes(
		out.AccessToken, out.RefreshToken))
}

// POST /api/sign-up
// -----------------------------------------------------------------------

func (h *Handler) postSignUp(ctx *gin.Context) {

	username := ctx.GetString(contextUsername)
	passwordHash := ctx.GetString(contextPassword)

	// ***

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(username, passwordHash))

	if err != nil {
		logger.Warning("%v", err)
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignUpPostRes(
		out.AccessToken, out.RefreshToken))
}

// PUT /api/sign-in
// -----------------------------------------------------------------------

func (h *Handler) putSignIn(ctx *gin.Context) {

	refreshToken := ctx.GetString(contextRefreshToken)

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.RefreshTokens(ctx, refreshToken)
	if err != nil {
		logger.Warning("%v", err)
		resWithErrorForClient(ctx, err) // new style!
		return
	}

	// ***

	ctx.JSON(http.StatusOK, dto.MakeSignUpPutRes(
		out.AccessToken, out.RefreshToken))
}

// middlewares
// -----------------------------------------------------------------------

// application/x-www-form-urlencoded?

func extractLoginAndPassword(ctx *gin.Context) {
	var (
		username = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // password hash in hex!
	)
	if len(username) == 0 || len(password) == 0 {
		resWithClientError(ctx,
			se.ValidateRequestParamsFailed,
			ErrEmptyRequestParameter,
		)
		return
	}

	// ***

	ctx.Set(contextPassword, password)
	ctx.Set(contextUsername, username)
}

func extractRefreshToken(ctx *gin.Context) {
	var (
		refreshToken = ctx.Request.FormValue("refresh-token")
	)
	if len(refreshToken) == 0 {
		resWithClientError(ctx,
			se.ValidateRequestParamsFailed, // ?
			ErrEmptyRequestParameterWithName("RefreshToken"),
		)
		return
	}

	// ***

	ctx.Set(contextRefreshToken, refreshToken)
}
