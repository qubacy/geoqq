package api

import (
	"fmt"
	"geoqq/internal/delivery/http/api/dto"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/pkg/logger"
	"geoqq/pkg/utility"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerUserRoutes() {
	myProfileRouter := h.router.Group("/my-profile", h.parseAnyForm)
	{
		myProfileRouter.GET("",
			h.userIdentityByHeader, h.userNotDeleted,
			h.getMyProfile,
		)

		myProfileRouter.PUT("",
			h.userIdentityByHeader, h.userNotDeleted,
			h.extractBodyForPutMyProfile,
			h.putMyProfile,
		)

		// deprecated!
		myProfileRouter.PUT("/with-attached-avatar",
			h.userIdentityByHeader, h.userNotDeleted,
			h.extractBodyForPutMyProfileWithAttachedAvatar,
			h.putMyProfileWithAttachedAvatar,
		)

		myProfileRouter.DELETE("",
			h.userIdentityByHeader, h.userNotDeleted,
			h.deleteMyProfile,
		)
	}

	// ***

	userRouter := h.router.Group("/user", h.parseAnyForm)
	{
		userRouter.GET("/:id",
			h.userIdentityByHeader, h.userNotDeleted,
			h.getUser,
		)
		userRouter.POST("",
			h.userIdentityByHeader, h.userNotDeleted,
			h.extractBodyForGetSomeUsers,
			h.getSomeUsers,
		)
	}
}

// my-profile
// -----------------------------------------------------------------------

// GET /api/my-profile
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
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	responseDto := dto.MakeMyProfileRes(userProfile)
	resJsonWithOK(ctx, responseDto)
}

// PUT /api/my-profile/with-attached-avatar
// -----------------------------------------------------------------------

func (h *Handler) extractBodyForPutMyProfileWithAttachedAvatar(ctx *gin.Context) {
	requestDto := dto.MyProfileWithAttachedAvatarPutReq{}

	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// *** validate json body

	if requestDto.Avatar != nil {
		if len(requestDto.Avatar.Content) == 0 {
			resWithClientError(ctx, ec.ValidateRequestParamsFailed, ErrEmptyBodyParameter)
			return
		}

		// validate avatar extension?
	}
	if requestDto.Security != nil {
		if len(requestDto.Security.Password) == 0 {
			resWithClientError(ctx, ec.ValidateRequestParamsFailed, ErrEmptyBodyParameter)
			return
		}
		if len(requestDto.Security.NewPassword) == 0 {
			resWithClientError(ctx, ec.ValidateRequestParamsFailed, ErrEmptyBodyParameter)
			return
		}
	}

	if requestDto.Description == nil { // as reset description!
		requestDto.Description = new(string)
		*requestDto.Description = ""
	}

	/*
		if requestDto.Privacy != nil {
		}
	*/

	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) putMyProfileWithAttachedAvatar(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// ***

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.MyProfileWithAttachedAvatarPutReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// ***

	err = h.services.UpdateUserProfileWithAvatar(ctx, userId, requestDto.ToInp())
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

func (h *Handler) extractBodyForPutMyProfile(ctx *gin.Context) {
	requestDto := dto.MyProfilePutReq{}

	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// *** validate json body

	if requestDto.Security != nil {
		if len(requestDto.Security.Password) == 0 {
			resWithClientError(ctx, ec.ValidateRequestParamsFailed, ErrEmptyBodyParameter)
			return
		}
		if len(requestDto.Security.NewPassword) == 0 {
			resWithClientError(ctx, ec.ValidateRequestParamsFailed, ErrEmptyBodyParameter)
			return
		}
	}

	/*
		if requestDto.Description == nil { // as reset description?
			requestDto.Description = new(string)
			*requestDto.Description = ""
		}
	*/

	/*
		if requestDto.Privacy != nil {
		}
	*/

	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) putMyProfile(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// ***

	anyRequestDto, _ := ctx.Get(contextRequestDto)
	requestDto, converted := anyRequestDto.(dto.MyProfilePutReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// ***

	err = h.services.UpdateUserProfile(ctx, userId, requestDto.ToInp())
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	ctx.Status(http.StatusOK)
}

// DELETE /api/my-profile
// -----------------------------------------------------------------------

func (h *Handler) deleteMyProfile(ctx *gin.Context) {
	userId := ctx.GetUint64(contextUserId)
	err := h.services.DeleteUserProfile(ctx, userId)
	if err != nil {
		resWithErrorForClient(ctx, err)
		return
	}

	ctx.Status(http.StatusOK)
}

// user
// -----------------------------------------------------------------------

// GET /api/user/{id}
// -----------------------------------------------------------------------

type uriParamsGetUser struct {
	Id uint64 `uri:"id" binding:"required"`
}

func (h *Handler) getUser(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// delivery --->

	uriParams := uriParamsGetUser{}
	if err := ctx.ShouldBindUri(&uriParams); err != nil {
		resWithClientError(ctx, ec.ParseRequestQueryParamsFailed, err)
		return
	}

	// <---> service

	publicUser, err := h.services.GetPublicUserById(ctx, userId, uriParams.Id)
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.MakeUserByIdResFromDomain(publicUser)
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	logger.Trace(fmt.Sprintf("%v", responseDto))
	resJsonWithOK(ctx, responseDto)
}

// GET /api/user
// -----------------------------------------------------------------------

func (h *Handler) extractBodyForGetSomeUsers(ctx *gin.Context) {
	requestDto := dto.SomeUsersReq{}
	if err := ctx.ShouldBindJSON(&requestDto); err != nil {
		resWithClientError(ctx, ec.ParseRequestJsonBodyFailed, err)
		return
	}

	// ***

	/*
		if len(requestDto.Ids) == 0 {
			resWithClientError(ctx, ec.ValidateRequestFailed, ErrEmptyBodyParameter)
			return
		}
	*/

	ctx.Set(contextRequestDto, requestDto)
}

func (h *Handler) getSomeUsers(ctx *gin.Context) {
	userId, clientCode, err := extractUserIdFromContext(ctx)
	if err != nil {
		resWithServerErr(ctx, clientCode, err)
		return
	}

	// delivery --->

	anyRequestDto, exists := ctx.Get(contextRequestDto)
	if !exists {
		resWithServerErr(ctx, ec.ServerError, ErrEmptyContextParam)
		return
	}
	requestDto, converted := anyRequestDto.(dto.SomeUsersReq)
	if !converted {
		resWithServerErr(ctx, ec.ServerError, ErrUnexpectedContextParam)
		return
	}

	// <---> services

	publicUsers, err := h.services.GetPublicUserByIds(ctx, userId,
		utility.ConvertSliceFloat64ToUint64(requestDto.Ids))
	if err != nil {
		side, code := ec.UnwrapErrorsToLastSideAndCode(err)
		resWithSideErr(ctx, side, code, err)
		return
	}

	// ---> delivery

	responseDto, err := dto.NewSomeUsersResFromDomain(publicUsers) // TODO: struct to pointer!
	if err != nil {
		resWithServerErr(ctx, ec.ServerError, err)
		return
	}

	logger.Trace(fmt.Sprintf("%v", responseDto))
	resJsonWithOK(ctx, responseDto)
}
