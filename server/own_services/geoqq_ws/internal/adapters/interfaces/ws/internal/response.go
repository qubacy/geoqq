package internal

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/serverSide"
	"net/http"
	"time"

	"github.com/lxzan/gws"
)

// ws
// -----------------------------------------------------------------------

func (c *WsEventHandler) resWithClientError(socket *gws.Conn, errorId int, err error) {
	shortErr := utl.UnwrapErrorsToLast(err)
	errMessage := serverSide.MakeMessage(serverSide.EventGeneralError,

		loadWithTrace(
			http.StatusBadRequest, errorId,
			shortErr, err))

	jsonBytes, err := json.Marshal(errMessage)
	if err != nil {
		logger.Error("%v", utl.NewFuncError(c.resWithClientError, err))
	}

	socket.SetWriteDeadline(time.Now().Add(c.writeTimeout))
	socket.WriteString(string(jsonBytes))
}
