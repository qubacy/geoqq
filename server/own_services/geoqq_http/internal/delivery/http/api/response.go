package api

import (
	ec "common/pkg/errorForClient/geoqq"
	httpEr "common/pkg/httpErrorResponse"
	"common/pkg/logger"
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

// wrappers!
// -----------------------------------------------------------------------

func resWithClientError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err)
	httpEr.ResWithClientError(ctx, errorId, err)
}

func resWithAuthError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err)
	httpEr.ResWithAuthError(ctx, errorId, err)
}

func resWithServerErr(ctx *gin.Context, errorId int, err error) {
	logger.Error("%v", err)
	httpEr.ResWithServerErr(ctx, errorId, err)
}

func resWithSideErr(ctx *gin.Context, side int, errorId int, err error) {
	httpCode := http.StatusInternalServerError // ?
	level := logger.LevelWarning

	switch side {
	case ec.Client:
		httpCode = http.StatusBadRequest
	case ec.Server:
		level = logger.LevelError
	}

	logger.To(level, "%v", err)
	httpEr.ResWithErr(ctx, httpCode, errorId, err)
}

// -----------------------------------------------------------------------

func resWithErrorForClient(ctx *gin.Context, err error) {
	side, code := ec.UnwrapErrorsToLastSideAndCode(err)
	resWithSideErr(ctx, side, code, err)
}
