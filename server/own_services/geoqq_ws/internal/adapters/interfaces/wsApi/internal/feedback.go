package internal

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"fmt"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	svrSide "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide/payload"
	"geoqq_ws/internal/application/ports/input"
	"time"

	ec "common/pkg/errorForClient/geoqq"

	"github.com/lxzan/gws"
)

func (w *WsEventHandler) initFbChans(ctxFb context.Context) {
	{
		w.initMateRequestsFb(ctxFb)
		w.initMateChatFb(ctxFb)
		w.initMateMessagesFb(ctxFb)
	}
	w.initGeoMessagesFb(ctxFb)
	w.initPublicUserFb(ctxFb)
}

// specific...
// -----------------------------------------------------------------------

func createFbName(baseObjectName string) string {
	return fmt.Sprintf("%v feedback", baseObjectName)
}

var (
	mateRequestFbName = createFbName("mate request")
	mateChatFbName    = createFbName("mate chat")
	mateMessageFbName = createFbName("mate message")
	publicUserFbName  = createFbName("public user")
	geoMessageFbName  = createFbName("geo message")
) // ?

func initFeedbackHandler[T input.UserIdHolder](w *WsEventHandler, ctx context.Context,
	fbChans []<-chan T, fbName, eventName string,
	prepareResponse func(userIdWith T) (any, error)) {
	sourceFunc := initFeedbackHandler[T]

	for i := range fbChans {
		go func(ch <-chan T) {
			for {
				select {
				case <-ctx.Done():
					logger.Info("%v stopped", fbName)
					return

				case pair := <-ch:
					socket, loaded := w.loadSocket(pair.GetUserId())
					if !loaded {
						break
					}

					// different logic!

					res, err := prepareResponse(pair)
					if err != nil {
						w.resWithServerError(socket, svrSide.EventGeneralError, // ?
							ec.ServerError, utl.NewFuncError(sourceFunc, err))
						return
					}

					// ***

					w.sendAnyToSocket(socket,
						eventName, res)
				}
			}
		}(fbChans[i])
	}
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initMateRequestsFb(ctx context.Context) {
	initFeedbackHandler(w, ctx,
		w.mateRequestUc.GetFbChansForMateRequest(),
		mateRequestFbName, serverSide.EventAddedMateRequest,
		func(userIdWith input.UserIdWithMateRequest) (any, error) {

			mateRequest := payload.MakeMateRequest(
				float64(userIdWith.MateRequestId),
				float64(userIdWith.SourceUserId)) // from!

			return mateRequest, nil
		})
}

func (w *WsEventHandler) initMateChatFb(ctx context.Context) {
	//sourceFunc := w.initMateChatFb

}

func (w *WsEventHandler) initMateMessagesFb(ctx context.Context) {
	sourceFunc := w.initMateMessagesFb
	initFeedbackHandler(w, ctx,
		w.mateMessageUc.GetFbChansForMateMessages(),
		mateMessageFbName, serverSide.EventAddedMateMessage,

		func(userIdWith input.UserIdWithMateMessage) (any, error) {

			// mm eq nil OR err eq nil!

			mm, err := payload.MateMessageFromDomain(userIdWith.MateMessage)
			return mm, utl.NewFuncErrorOnlyForNotNilWithPostProc(
				sourceFunc, err, func(err error) { logger.Error("%v", err) })
		})
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initGeoMessagesFb(ctx context.Context) {
	sourceFunc := w.initGeoMessagesFb
	initFeedbackHandler(w, ctx,
		w.geoMessageUc.GetFbChansForGeoMessages(),
		geoMessageFbName, serverSide.EventAddedGeoMessage,

		func(userIdWith input.UserIdWithGeoMessage) (any, error) {
			gm, err := payload.GeoMessageFromDomain(userIdWith.GeoMessage)
			return gm, utl.NewFuncErrorOnlyForNotNilWithPostProc(
				sourceFunc, err, func(err error) { logger.Error("%v", err) })
		})
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initPublicUserFb(ctx context.Context) {
	sourceFunc := w.initPublicUserFb
	initFeedbackHandler(w, ctx,
		w.publicUserUsecase.GetFbChansForPublicUser(),
		publicUserFbName, serverSide.EventUpdatedPublicUser,

		func(userIdWith input.UserIdWithPublicUser) (any, error) {
			pu, err := payload.PublicUserFromDomain(userIdWith.PublicUser)
			return pu, utl.NewFuncErrorOnlyForNotNilWithPostProc(
				sourceFunc, err, func(err error) { logger.Error("%v", err) })
		})
}

// wrapper
// -----------------------------------------------------------------------

func (w *WsEventHandler) sendAnyToSocket(socket *gws.Conn, eventName string, a any) {
	msg := serverSide.Message{
		Event:   eventName,
		Payload: a,
	}
	jsonBytes, err := json.Marshal(msg)
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

func (w *WsEventHandler) loadSocket(userId uint64) (*gws.Conn, bool) {
	value, loaded := w.userSockets.Load(userId)
	if !loaded {
		logger.Error("%v", ErrSocketNotFoundByUserIdInMapWith(userId))
		return nil, false
	}

	socket, converted := value.(*gws.Conn)
	if !converted {
		logger.Error("%v", ErrAnyNotConvertedToSocket)
	}

	return socket, true // ok
}
