package api

import (
	"geoqq/internal/delivery/http/api/dto"
	se "geoqq/pkg/sideError/impl"
	"geoqq/pkg/utility"
	"net/http"

	"github.com/gin-gonic/gin"
)

func resWithErr(ctx *gin.Context, httpCode, errorId int, err error) {
	err = utility.UnwrapErrorsToLast(err)
	ctx.JSON(httpCode, dto.MakeResWithError(errorId, err))
}

func resWithClientError(ctx *gin.Context, errorId int, err error) {
	resWithErr(ctx, http.StatusBadRequest, errorId, err)
}

func resWithServerErr(ctx *gin.Context, errorId int, err error) {
	resWithErr(ctx, http.StatusInternalServerError, errorId, err)
}

func resWithSideErr(ctx *gin.Context, side uint, errorId int, err error) {
	httpCode := http.StatusInternalServerError // ?

	switch side {
	case se.Client:
		httpCode = http.StatusBadRequest
	case se.Server:
		httpCode = http.StatusInternalServerError
	}

	resWithErr(ctx, httpCode, errorId, err)
}
