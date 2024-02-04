package api

import (
	"geoqq/internal/delivery/http/api/dto"
	se "geoqq/pkg/errorForClient/impl"
	"net/http"

	"github.com/gin-gonic/gin"
)

func (h *Handler) registerUserRoutes() {
	myProfileRouter := h.router.Group("/my-profile", h.userIdentityForGetRequest)
	{
		myProfileRouter.GET("", h.getMyProfile)
		myProfileRouter.PUT("", h.putMyProfile)
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

}

// user
// -----------------------------------------------------------------------

func (h *Handler) getUser(ctx *gin.Context) {

}

func (h *Handler) getSomeUsers(ctx *gin.Context) {

}

// private
// -----------------------------------------------------------------------
