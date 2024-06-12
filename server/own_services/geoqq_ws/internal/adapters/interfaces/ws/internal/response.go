package internal

import (
	httpEr "common/pkg/httpErrorResponse"
	"common/pkg/logger"

	"github.com/gin-gonic/gin"
)

func resWithAuthError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err) // !
	httpEr.ResWithAuthError(ctx, errorId, err)
}
