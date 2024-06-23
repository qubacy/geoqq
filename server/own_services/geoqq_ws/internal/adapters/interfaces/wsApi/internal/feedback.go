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
	w.initMateMessagesFb(ctxFb)
	w.initGeoMessagesFb(ctxFb)
}

// specific...
// -----------------------------------------------------------------------

func (w *WsEventHandler) initMateMessagesFb(ctxFb context.Context) {
	sourceFunc := w.initMateMessagesFb
	const fbName = "mate messages feedback"

	fbChans := w.mateMessageUc.GetFbChansForMateMessages()
	for i := range fbChans {
		go func(ch <-chan input.UserIdWithMateMessage) {
			for {
				select {
				case <-ctxFb.Done():
					logger.Info("%v stopped", fbName)
					return

				case pair := <-ch:
					value, loaded := w.userSockets.Load(pair.UserId)
					if !loaded {
						logger.Error("%v", ErrSocketNotFoundByUserIdInMapWith(pair.UserId))
						break
					}
					socket := value.(*gws.Conn)

					// ***

					mm, err := payload.MateMessageFromDomain(pair.MateMessage)
					if err != nil {
						w.resWithServerError(socket, svrSide.EventGeneralError,
							ec.ServerError, utl.NewFuncError(sourceFunc, err))
						return
					}

					w.sendAnyToSocket(socket, mm)
				}
			}
		}(fbChans[i])
	}
}

func (w *WsEventHandler) initGeoMessagesFb(ctxFb context.Context) {
	sourceFunc := w.initGeoMessagesFb
	const fbName = "geo messages feedback"

	fbChans := w.geoMessageUc.GetFbChansForGeoMessages()
	for i := range fbChans {
		go func(ch <-chan input.UserIdWithGeoMessage) {
			for {
				select {
				case <-ctxFb.Done():
					logger.Info("%v stopped", fbName)
					return

				case pair := <-ch:
					value, loaded := w.userSockets.Load(pair.UserId)
					if !loaded {
						logger.Error("%v", ErrSocketNotFoundByUserIdInMapWith(pair.UserId))
						break
					}
					socket := value.(*gws.Conn)

					// ***

					gm, err := payload.GeoMessageFromDomain(pair.GeoMessage)
					if err != nil {
						w.resWithServerError(socket, svrSide.EventGeneralError,
							ec.ServerError, utl.NewFuncError(sourceFunc, err))
						return
					}

					w.sendAnyToSocket(socket, gm)
				}
			}
		}(fbChans[i])
	}
}

// wrapper
// -----------------------------------------------------------------------

func (w *WsEventHandler) sendAnyToSocket(socket *gws.Conn, a any) {
	jsonBytes, err := json.Marshal(a)
	if err != nil {
		w.resWithServerError(socket, svrSide.EventGeneralError,
			ec.ServerError, utl.NewFuncError(w.sendAnyToSocket, err))
		return
	}
	w.sendBytesToSocket(socket, jsonBytes)
}

func (w *WsEventHandler) sendBytesToSocket(socket *gws.Conn, bytes []byte) {
	err := utl.RunFuncsRetErr(
		func() error { return socket.SetWriteDeadline(time.Now().Add(w.writeTimeout)) },
		func() error { return socket.WriteString(string(bytes)) })

	if err != nil {
		logger.Warning("%v", utl.NewFuncError(w.sendBytesToSocket, err))
	}
}
