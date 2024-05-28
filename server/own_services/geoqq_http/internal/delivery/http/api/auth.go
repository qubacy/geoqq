package api

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	"geoqq_http/internal/delivery/http/api/dto"
	"geoqq_http/internal/service"
	serviceDto "geoqq_http/internal/service/dto"

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
	login := ctx.GetString(contextLogin)
	passwordHash := ctx.GetString(contextPassword)

	// ***

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.SignIn(ctx,
		serviceDto.MakeSignInInp(login, passwordHash))

	if err != nil { // error may belong to different sides!
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ***

	logger.Trace("access token: %v", out.AccessToken)
	logger.Trace("refresh token: %v", out.RefreshToken)

	resJsonWithOK(ctx, dto.MakeSignInPostRes(
		out.AccessToken, out.RefreshToken))
}

// POST /api/sign-up
// -----------------------------------------------------------------------

func (h *Handler) postSignUp(ctx *gin.Context) {

	login := ctx.GetString(contextLogin)
	passwordHash := ctx.GetString(contextPassword)

	// ***

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.SignUp(ctx,
		serviceDto.MakeSignUpInp(login, passwordHash))

	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	// ***

	resJsonWithOK(ctx, dto.MakeSignUpPostRes(
		out.AccessToken, out.RefreshToken))
}

// PUT /api/sign-in
// -----------------------------------------------------------------------

func (h *Handler) putSignIn(ctx *gin.Context) {

	refreshToken := ctx.GetString(contextRefreshToken)
	logger.Trace("refresh token: %v", refreshToken)

	ctx.Set(service.AuthServiceContextClientIp, ctx.ClientIP())
	out, err := h.services.RefreshTokens(ctx, refreshToken)
	if err != nil {
		resWithErrorForClient(ctx, err) // new style!
		return
	}

	// ***

	resJsonWithOK(ctx, dto.MakeSignUpPutRes(
		out.AccessToken, out.RefreshToken))
}

// middlewares
// -----------------------------------------------------------------------

// application/x-www-form-urlencoded?

func extractLoginAndPassword(ctx *gin.Context) {
	var (
		login    = ctx.Request.FormValue("login")
		password = ctx.Request.FormValue("password") // password hash in hex!
	)
	if len(login) == 0 || len(password) == 0 {
		resWithClientError(ctx,
			ec.ValidateRequestParamsFailed,
			ErrEmptyRequestParameter,
		)
		return
	}

	// ***

	ctx.Set(contextPassword, password)
	ctx.Set(contextLogin, login)
}

func extractRefreshToken(ctx *gin.Context) {
	var (
		refreshToken = ctx.Request.FormValue("refresh-token")
	)
	if len(refreshToken) == 0 {
		resWithClientError(ctx,
			ec.ValidateRequestParamsFailed, // ?
			ErrEmptyRequestParameterWithName("RefreshToken"),
		)
		return
	}

	// ***

	ctx.Set(contextRefreshToken, refreshToken)
}
