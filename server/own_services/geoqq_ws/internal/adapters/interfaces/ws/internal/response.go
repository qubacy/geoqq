package internal

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/serverSide"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/serverSide/payload"
	"net/http"
	"time"

	"github.com/lxzan/gws"
)

func (c *Handler) resWithClientError(socket *gws.Conn, errorId int, err error) {
	shortErr := utl.UnwrapErrorsToLast(err)
	errMessage := serverSide.MakeMessage(serverSide.EventGeneralError,
		payload.MakeErrorPayloadWithTrace(
			http.StatusBadRequest, errorId,
			shortErr, err))

	jsonBytes, err := json.Marshal(errMessage)
	if err != nil {
		logger.Error("%v", utl.NewFuncError(c.resWithClientError, err))
	}

	socket.SetWriteDeadline(time.Now().Add(c.writeTimeout))
	socket.WriteString(string(jsonBytes))
}
