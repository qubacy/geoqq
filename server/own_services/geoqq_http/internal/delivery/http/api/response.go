package api

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	"common/pkg/utility"
	"geoqq_http/internal/delivery/http/api/dto"
	"net/http"

	"github.com/gin-gonic/gin"
)

func resJsonWithOK(ctx *gin.Context, obj any) {
	logger.Trace("%v", obj)

	ctx.JSON(http.StatusOK, obj)
}

func resWithOK(ctx *gin.Context) {
	ctx.Status(http.StatusOK)
}

func resWithErr(ctx *gin.Context, httpCode, errorId int, err error) {
	shortErr := utility.UnwrapErrorsToLast(err) // <--- very first reason!
	ctx.JSON(httpCode, dto.MakeResWithTraceError(
		errorId, shortErr, err))

	ctx.Abort() // ?
}

// -----------------------------------------------------------------------

func resWithClientError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err)
	resWithErr(ctx, http.StatusBadRequest, errorId, err)
}

func resWithAuthError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err)
	resWithErr(ctx, http.StatusUnauthorized, errorId, err)
}

func resWithServerErr(ctx *gin.Context, errorId int, err error) {
	logger.Error("%v", err)
	resWithErr(ctx, http.StatusInternalServerError, errorId, err)
}

func resWithSideErr(ctx *gin.Context, side int, errorId int, err error) {
	httpCode := http.StatusInternalServerError // ?
	level := logger.LevelWarning

	switch side {
	case ec.Client:
		httpCode = http.StatusBadRequest
	case ec.Server:
		httpCode = http.StatusInternalServerError
		level = logger.LevelError
	}

	logger.To(level, "%v", err)
	resWithErr(ctx, httpCode, errorId, err)
}

// -----------------------------------------------------------------------

func resWithErrorForClient(ctx *gin.Context, err error) {
	side, code := ec.UnwrapErrorsToLastSideAndCode(err)
	resWithSideErr(ctx, side, code, err)
}
