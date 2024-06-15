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

	writeTimeout time.Duration
	readTimeout  time.Duration

	tpExtractor token.TokenPayloadExtractor
	clients     sync.Map

	router map[string]PayloadHandler

	// services/usecases

	userUc input.UserUsecase
}

func NewWsEventHandler(
	enablePing bool,
	pingTimeout, pingInterval time.Duration,
	writeTimeout, readTimeout time.Duration,
	tpExtractor token.TokenPayloadExtractor,
	userUc input.UserUsecase) *WsEventHandler {

	hh := &WsEventHandler{
		enablePing:   enablePing,
		pingTimeout:  pingTimeout, // and pong!
		pingInterval: pingInterval,

		writeTimeout: writeTimeout,
		readTimeout:  readTimeout,

		tpExtractor: tpExtractor,
		clients:     sync.Map{}, // map[*gws.Conn]Client

		userUc: userUc,
	}

	hh.initRouter()
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

// impl interface for gws.Event!
// -----------------------------------------------------------------------

/*
	type Event interface {
		OnOpen(socket *Conn)
		OnClose(socket *Conn, err error)
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

	// ***

	c := NewClient(socket, w.tpExtractor, userId)
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

	if w.enablePing {
		value, loaded := w.clients.LoadAndDelete(socket)
		if !loaded { // impossible!?
			return
		}

		client := value.(*Client)
		client.pingCancel()
	} else {
		w.clients.Delete(socket)
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

	ph(client, clientMessage.Payload)
}
