package internal

import (
	httpEr "common/pkg/httpErrorResponse"
	"common/pkg/logger"
	"common/pkg/utility"
	"encoding/json"
	svrSide "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide/payload"
	"time"

	"github.com/gin-gonic/gin"
)

// http
// -----------------------------------------------------------------------

func httpResWithAuthError(ctx *gin.Context, errorId int, err error) {
	logger.Warning("%v", err) // !
	httpEr.ResWithAuthError(ctx, errorId, err)
}

func httpResWithServerError(ctx *gin.Context, errorId int, err error) {
	logger.Error("%v", err)
	httpEr.ResWithServerErr(ctx, errorId, err) // ?
}

// ws
// -----------------------------------------------------------------------

func (w *WsEventHandler) resWithErr(client *Client, httpCode, errorId int, err error) {
	sourceFunc := w.resWithErr
	defer client.socket.WriteClose(1000, nil)

	if err := client.socket.SetWriteDeadline(time.Now().Add(w.pingTimeout)); err != nil {
		logger.Warning("%v", utility.NewFuncError(sourceFunc, err))
		return
	}

	shortErr := utility.UnwrapErrorsToLast(err)
	ssMsg := svrSide.MakeMessage(svrSide.EventGeneralError,
		payload.MakeErrorPayloadWithTrace(httpCode, errorId, shortErr, err))

	jsonBytes, err := json.Marshal(ssMsg)
	if err != nil { // impossible!
		logger.Error("%v", utility.NewFuncError(sourceFunc, err))
		return
	}

	if err = client.socket.WriteString(string(jsonBytes)); err != nil {
		logger.Warning("%v", utility.NewFuncError(sourceFunc, err))
		return
	}
}
