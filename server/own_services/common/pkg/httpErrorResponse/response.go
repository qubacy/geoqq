package httpErrorResponse

import (
	"common/pkg/httpErrorResponse/dto"
	"common/pkg/utility"
	"net/http"

	"github.com/gin-gonic/gin" // !
)

func ResWithClientError(ctx *gin.Context, errorId int, err error) {
	ResWithErr(ctx, http.StatusBadRequest, errorId, err)
}

func ResWithAuthError(ctx *gin.Context, errorId int, err error) {
	ResWithErr(ctx, http.StatusUnauthorized, errorId, err)
}

func ResWithServerErr(ctx *gin.Context, errorId int, err error) {
	ResWithErr(ctx, http.StatusInternalServerError, errorId, err)
}

func ResWithErr(ctx *gin.Context, httpCode, errorId int, err error) {
	shortErr := utility.UnwrapErrorsToLast(err) // <--- very first reason!
	ctx.JSON(httpCode, dto.MakeResWithTraceError(
		errorId, shortErr, err))

	ctx.Abort() // ?
}
