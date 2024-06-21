package internal

import (
	httpEr "common/pkg/httpErrorResponse"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"encoding/json"
	svrSide "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide/payload"
	"net/http"
	"time"

	ec "common/pkg/errorForClient/geoqq"

	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
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

func (w *WsEventHandler) resWithOK(socket *gws.Conn, eventName string) {
	sourceFunc := w.resWithOK
	ssMsg := svrSide.MakeMessage(eventName, nil)

	jsonBytes, err := json.Marshal(ssMsg)
	if err != nil { // impossible!
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		socket.WriteClose(1000, nil)
		return
	}

	err = utl.RunFuncsRetErr(
		func() error { return socket.SetWriteDeadline(time.Now().Add(w.writeTimeout)) },
		func() error { return socket.WriteString(string(jsonBytes)) })
	if err != nil {
		logger.Warning("%v", utl.NewFuncError(sourceFunc, err))
		socket.WriteClose(1000, nil)
	}
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) resWithServerError(socket *gws.Conn,
	eventName string, errorId int, err error) {
	logger.Error("%v", err)

	const httpCode = http.StatusInternalServerError
	w.resWithErr(socket, eventName, httpCode, errorId, err)
}

func (w *WsEventHandler) resWithClientError(socket *gws.Conn,
	eventName string, errorId int, err error) {
	logger.Warning("%v", err)

	const httpCode = http.StatusBadRequest
	w.resWithErr(socket, eventName, httpCode, errorId, err)
}

func (w *WsEventHandler) resWithAuthError(socket *gws.Conn,
	eventName string, errorId int, err error) {
	logger.Warning("%v", err)

	const httpCode = http.StatusUnauthorized
	w.resWithErr(socket, eventName, httpCode, errorId, err)
}

func (w *WsEventHandler) resWithErr(socket *gws.Conn,
	eventName string,
	httpCode, errorId int, err error) {

	sourceFunc := w.resWithErr
	defer socket.WriteClose(1000, nil)

	shortErr := utl.UnwrapErrorsToLast(err)
	ssMsg := svrSide.MakeMessage(
		eventName,
		payload.MakeErrorPayloadWithTrace(httpCode, errorId, shortErr, err))

	jsonBytes, err := json.Marshal(ssMsg)
	if err != nil { // impossible!
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		return
	}

	err = utl.RunFuncsRetErr(
		func() error { return socket.SetWriteDeadline(time.Now().Add(w.writeTimeout)) },
		func() error { return socket.WriteString(string(jsonBytes)) })
	if err != nil {
		logger.Warning("%v", utl.NewFuncError(sourceFunc, err))
	}
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) resWithErrorForClient(socket *gws.Conn,
	eventName string, err error) {

	side, errorId := ec.UnwrapErrorsToLastSideAndCode(err)

	httpCode := http.StatusInternalServerError
	level := logger.LevelWarning

	switch side {
	case ec.Client:
		httpCode = http.StatusBadRequest
	case ec.Server:
		level = logger.LevelError
	}

	logger.To(level, "%v", err)
	w.resWithErr(socket, eventName,
		httpCode, errorId, err)
}

func (w *WsEventHandler) commonRes(socket *gws.Conn,
	eventOk, eventFl string, err error /* error for client */) {

	if err != nil {
		w.resWithErrorForClient(socket, eventFl, err) // unwrap!
		return
	}

	w.resWithOK(socket, eventOk)
}
