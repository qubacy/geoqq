package internal

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	svrSide "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide/payload"
	"geoqq_ws/internal/application/ports/input"
	"time"

	ec "common/pkg/errorForClient/geoqq"

	"github.com/lxzan/gws"
)

func (w *WsEventHandler) initFbChans(ctxFb context.Context) {
	{
		fbChans := w.mateUc.GetFbChansForMateMessages()
		for i := range fbChans {
			go func(ch <-chan input.UserIdWithMateMessage) {
				for {
					select {
					case <-ctxFb.Done():
						logger.Info("mate messages feedback stopped")
						return

					case pair := <-ch:
						value, loaded := w.userSockets.Load(pair.UserId)
						if !loaded {
							logger.Error("%v", ErrSocketNotFoundByUserIdInMapWith(pair.UserId))
							break
						}
						socket := value.(*gws.Conn)

						// ***

						mm, err := payload.MateMessageFromDomain(&pair.MateMsg)
						if err != nil {
							w.resWithServerError(socket, svrSide.EventGeneralError,
								ec.ServerError, err)
							return
						}
						jsonBytes, err := json.Marshal(mm)
						if err != nil {
							w.resWithServerError(socket, svrSide.EventGeneralError,
								ec.ServerError, err)
							return
						}

						err = utl.RunFuncsRetErr(
							func() error { return socket.SetWriteDeadline(time.Now().Add(w.writeTimeout)) },
							func() error { return socket.WriteString(string(jsonBytes)) })

						if err != nil {
							logger.Warning("%v", err)
						}
					}
				}
			}(fbChans[i])
		}
	}
}

func initMateMessageFb() {

}
