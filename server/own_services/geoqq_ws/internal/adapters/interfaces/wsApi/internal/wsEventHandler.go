package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	"common/pkg/token"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide"
	svrSide "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	"geoqq_ws/internal/application/ports/input"
	"sync"
	"time"

	"github.com/lxzan/gws"
)

type WsEventHandler struct {
	enablePing   bool
	pingTimeout  time.Duration
	pingInterval time.Duration

	readTimeout   time.Duration
	writeTimeout  time.Duration
	handleTimeout time.Duration

	tpExtractor token.TokenPayloadExtractor
	clients     sync.Map // map[*gws.Conn]Client
	userSockets sync.Map // map[uint64]*gws.Conn

	router map[string]PayloadHandler

	// services/usecases

	userUc        input.UserUsecase
	mateMessageUc input.MateMessageUsecase
	onlineUsersUc input.OnlineUsersUsecase
	geoMessageUc  input.GeoMessageUsecase

	ctxFb    context.Context
	cancelFb context.CancelFunc
}

func NewWsEventHandler(p *Params) *WsEventHandler {
	ctxFb, cancelFb := context.WithCancel(
		context.Background())

	hh := &WsEventHandler{
		enablePing:   p.EnablePing,
		pingTimeout:  p.PingTimeout, // and pong!
		pingInterval: p.PingInterval,

		readTimeout:   p.ReadTimeout,
		writeTimeout:  p.WriteTimeout,
		handleTimeout: p.HandleTimeout,

		tpExtractor: p.TpExtractor,
		clients:     sync.Map{},
		userSockets: sync.Map{},

		userUc:        p.UserUc,
		onlineUsersUc: p.OnlineUsersUc,
		mateMessageUc: p.MateMessageUc,
		geoMessageUc:  p.GeoMessageUc,

		ctxFb:    ctxFb,
		cancelFb: cancelFb,
	}

	hh.initRouter()
	hh.initFbChans(ctxFb)

	return hh
}

func (w *WsEventHandler) initRouter() {
	w.router = map[string]PayloadHandler{
		"update_user_location": w.updateUserLocation,
		"add_geo_message":      w.addGeoMessage,
		"add_mate_message":     w.addMateMessage,
		//...
	}
}

func (w *WsEventHandler) Stop() {

	// If `f` returns false,
	// 	range stops the iteration.

	w.clients.Range(func(key, value any) bool {
		conn := key.(*gws.Conn)
		conn.WriteClose(CloseCode_ServerStop, nil) // need timeout?

		return true
	})
}

// impl interface for gws.Event!
// -----------------------------------------------------------------------

/*
	type Event interface {
		OnOpen(socket *Conn)
		OnClose(socket *Conn, err error) // received a close frame or input/output error occurs
		OnPing(socket *Conn, payload []byte)
		OnPong(socket *Conn, payload []byte)
		OnMessage(socket *Conn, message *Message)
	}
*/

func (w *WsEventHandler) OnOpen(socket *gws.Conn) {
	ss := socket.Session()
	rawUserId, exist := ss.Load(contextUserId)

	var userId uint64
	err := utl.RunFuncsRetErr(
		func() error {
			if !exist {
				return ErrSessionStorageHasNoUserId
			}
			return nil
		}, func() error {
			var ok bool
			userId, ok = rawUserId.(uint64)
			if !ok {
				return ErrUserIdNotConvertedToUint64
			}
			return nil
		},
	)
	if err != nil {
		w.resWithServerError(socket, svrSide.EventGeneralError,
			ec.ServerError, err)
		return
	}

	logger.Info("connection opened (addr: %v, userId: %v)",
		socket.RemoteAddr(), userId)
	w.onlineUsersUc.SetUsersToOnline(userId)

	// ***

	c := NewClient(socket, w.tpExtractor, userId)

	w.userSockets.Store(userId, socket)
	w.clients.Store(socket, c)

	if w.enablePing {
		c.pingContext, c.pingCancel =
			context.WithCancel(context.Background())

		/*
			Can be put into a separate function,
				but the context includes many dependencies.
		*/
		go func() {
			for {
				select {
				case <-c.pingContext.Done():
					return

				case <-time.After(w.pingInterval):
					err := utl.RunFuncsRetErr(
						func() error { return socket.SetDeadline(time.Now().Add(w.pingTimeout)) },
						func() error { return socket.WritePing(nil) })
					if err != nil {
						logger.Warning("%v in ping routine", err) // ?
					}
				}
			}
		}()
	}
}

func (w *WsEventHandler) OnClose(socket *gws.Conn, err error) {
	logger.Info("connection closed (addr: %v)", socket.RemoteAddr())
	if err != nil {
		logger.Warning("%v", utl.NewFuncError(w.OnClose, err))
	}

	// ***

	value, loaded := w.clients.LoadAndDelete(socket)
	if !loaded { // impossible!?
		return
	}

	client := value.(*Client)
	w.userSockets.Delete(client.userId)
	w.onlineUsersUc.RemoveUsersFromOnline(client.userId)

	if w.enablePing {
		client.pingCancel()
	}
}

func (w *WsEventHandler) OnPing(socket *gws.Conn, payload []byte) {
	err := utl.RunFuncsRetErr(
		func() error { return socket.SetDeadline(time.Now().Add(w.pingTimeout)) },
		func() error { return socket.WritePong(nil) })
	if err != nil {
		logger.Warning("%v", w.OnPing)
	}
}

func (c *WsEventHandler) OnPong(socket *gws.Conn, payload []byte) {}

// to message handler!
// -----------------------------------------------------------------------

func (w *WsEventHandler) OnMessage(socket *gws.Conn, message *gws.Message) {
	clientMessage := clientSide.Message{}
	rawMessage := string(message.Bytes())
	logger.Trace("%v", rawMessage)

	if err := json.Unmarshal(message.Bytes(), &clientMessage); err != nil {
		w.resWithClientError(socket, svrSide.EventParseError,
			ec.ParseRequestJsonBodyFailed, utl.NewFuncError(w.OnMessage, err))
		return
	}

	actionName := clientMessage.Action
	ph, ok := w.router[actionName]
	if !ok {
		w.resWithClientError(socket, svrSide.EventParseError,
			ec.Parse_UnknownAction, ErrUnknownActionWithName(actionName))
		return
	}

	// ***

	eventFailedName := svrSide.MakeEventWithPostfix(
		clientMessage.Action, svrSide.PostfixFailed)
	value, ok := w.clients.Load(socket)
	if !ok {
		w.resWithServerError(socket, eventFailedName,
			ec.ServerError, ErrClientNotFoundBySocketInMap)
		return
	}

	client := value.(*Client) // there cannot be other types!
	if err := client.assertUserIdentity(clientMessage.AccessToken); err != nil {
		w.resWithAuthError(socket, eventFailedName,
			ec.ValidateAccessTokenFailed, utl.NewFuncError(w.OnMessage, err)) // ?
		return
	}

	// ***

	// TODO: !!!!

	ctx := context.Background()
	ctx, _ = context.WithTimeout(ctx, w.handleTimeout)
	ph(ctx, client, clientMessage.Payload)
}
