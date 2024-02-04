package api

import (
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/pkg/errorForClient/impl"
	se "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerUserRoutes() {
	myProfileRouter := h.router.Group("/my-profile")
	{
		myProfileRouter.GET("", h.parseAnyForm, h.userIdentityForGetRequest, h.getMyProfile)
		myProfileRouter.PUT("", h.parseAnyForm, h.extractBodyForPutMyProfile,
			h.userIdentityByContextData, h.putMyProfile)
	}

	// ***

	userRouter := h.router.Group("/user", h.userIdentityForGetRequest)
	{
		userRouter.GET("/:id", h.getUser)
		userRouter.GET("", h.getSomeUsers)
	}
}

// my-profile
// -----------------------------------------------------------------------

func (h *Handler) getMyProfile(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// *** work with service

	userProfile, err := h.services.GetUserProfile(ctx, userId)
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.JSON(http.StatusOK,
		dto.MakeMyProfileRes(userProfile))
}

func (h *Handler) putMyProfile(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// ***

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, se.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.MyProfilePutReq)
	if !converted {
		resWithServerErr(ctx, se.ServerError, ErrUnexpectedContextParam)
		return
	}

	// ***

	err = h.services.UpdateUserProfile(ctx, userId, requestDto.ToInp())
	if err != nil {
		side, code := se.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}

func (h *Handler) extractBodyForPutMyProfile(ctx *gin.Context) {
	requestDto := dto.MyProfilePutReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// *** validate json body

	if len(requestDto.AccessToken) == 0 {
		resWithClientError(ctx, se.ValidateRequestFailed, ErrEmptyBodyParameter)
		return
	}
	if requestDto.Avatar != nil {
		if len(requestDto.Avatar.Content) == 0 {
			resWithClientError(ctx, se.ValidateRequestFailed, ErrEmptyBodyParameter)
			return
		}
	}
	if requestDto.Security != nil {
		if len(requestDto.Security.Password) == 0 {
			resWithClientError(ctx, se.ValidateRequestFailed, ErrEmptyBodyParameter)
			return
		}
		if len(requestDto.Security.NewPassword) == 0 {
			resWithClientError(ctx, se.ValidateRequestFailed, ErrEmptyBodyParameter)
			return
		}
	}

	// ***

	ctx.Set(contextAccessToken, requestDto.AccessToken)
	ctx.Set(contextRequestDto, requestDto)
}

// user
// -----------------------------------------------------------------------

func (h *Handler) getUser(ctx *gin.Context) {

}

func (h *Handler) getSomeUsers(ctx *gin.Context) {

}

// private
// -----------------------------------------------------------------------
