package internal

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"fmt"

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

func initFeedbackHandler[T input.UserIdWithEvent](w *WsEventHandler, ctx context.Context,
	fbChans []<-chan T, fbName string,
	prepareRes func(userIdWith T) (*svrSide.Message, error)) {
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

					msg, err := prepareRes(pair)
					if err != nil {
						w.resWithServerError(socket, svrSide.EventGeneralError, // ?
							ec.ServerError, utl.NewFuncError(sourceFunc, err))
						return
					}

					// ***

					w.sendMessageToSocket(socket, msg)
				}
			}
		}(fbChans[i])
	}
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initMateRequestsFb(ctx context.Context) {
	initFeedbackHandler(w, ctx,
		w.mateRequestUc.GetFbChansForMateRequest(), mateRequestFbName,
		func(ue input.UserIdWithMateRequest) (*svrSide.Message, error) {

			mateRequest := payload.MakeMateRequest(
				float64(ue.MateRequestId),
				float64(ue.SourceUserId)) // from!

			_ = ue.GetEvent() // ignore!

			return svrSide.NewMessage(
				svrSide.EventAddedMateRequest,
				mateRequest), nil
		})
}

func (w *WsEventHandler) initMateChatFb(ctx context.Context) {
	sourceFunc := w.initMateChatFb
	initFeedbackHandler(w, ctx,
		w.mateChatUc.GetFbChansForMateChat(), mateChatFbName,
		func(ue input.UserIdWithMateChat) (*svrSide.Message, error) {
			mateChat, err := payload.MateChatFromDomain(ue.MateChat)

			eventName := ""
			switch ue.GetEvent() {
			case input.EventAdded:
				eventName = svrSide.EventAddedMateChat
			case input.EventUpdated:
				eventName = svrSide.EventUpdatedMateChat
			}

			return svrSide.NewMessage(eventName, mateChat),
				utl.NewFuncErrorOnlyForNotNilWithPostProc(
					sourceFunc, err, logger.AboutError)
		})
}

func (w *WsEventHandler) initMateMessagesFb(ctx context.Context) {
	sourceFunc := w.initMateMessagesFb
	initFeedbackHandler(w, ctx,
		w.mateMessageUc.GetFbChansForMateMessages(), mateMessageFbName,
		func(ue input.UserIdWithMateMessage) (*svrSide.Message, error) {

			// mm eq nil OR err eq nil!

			_ = ue.GetEvent() // ignore!
			eventName := svrSide.EventAddedMateMessage

			mm, err := payload.MateMessageWithChatFromDomain(ue.MateMessage)
			return svrSide.NewMessage(eventName, mm),
				utl.NewFuncErrorOnlyForNotNilWithPostProc(
					sourceFunc, err, logger.AboutError)
		})
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initGeoMessagesFb(ctx context.Context) {
	sourceFunc := w.initGeoMessagesFb
	initFeedbackHandler(w, ctx,
		w.geoMessageUc.GetFbChansForGeoMessages(), geoMessageFbName,

		func(ue input.UserIdWithGeoMessage) (*svrSide.Message, error) {
			gm, err := payload.GeoMessageFromDomain(ue.GeoMessage)

			_ = ue.GetEvent() // ignore!
			eventName := svrSide.EventAddedGeoMessage

			return svrSide.NewMessage(eventName, gm),
				utl.NewFuncErrorOnlyForNotNilWithPostProc(
					sourceFunc, err, logger.AboutError)
		})
}

// -----------------------------------------------------------------------

func (w *WsEventHandler) initPublicUserFb(ctx context.Context) {
	sourceFunc := w.initPublicUserFb
	initFeedbackHandler(w, ctx,
		w.publicUserUsecase.GetFbChansForPublicUser(), publicUserFbName,

		func(ue input.UserIdWithPublicUser) (*svrSide.Message, error) {
			pu, err := payload.PublicUserFromDomain(ue.PublicUser)

			_ = ue.GetEvent() // ignore!
			eventName := svrSide.EventUpdatedPublicUser

			return svrSide.NewMessage(eventName, pu),
				utl.NewFuncErrorOnlyForNotNilWithPostProc(
					sourceFunc, err, logger.AboutError)
		})
}

// wrappers
// -----------------------------------------------------------------------

func (w *WsEventHandler) sendAnyToSocket(socket *gws.Conn, eventName string, payload any) {
	msg := svrSide.Message{
		Event:   eventName,
		Payload: payload, // wrapping!
	}
	w.sendMessageToSocket(socket, &msg)
}

func (w *WsEventHandler) sendMessageToSocket(socket *gws.Conn, msg *svrSide.Message) { // copy!
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
