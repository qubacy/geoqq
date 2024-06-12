package internal

import (
	"common/pkg/logger"
	"common/pkg/token"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide"
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
}

func NewWsEventHandler(
	enablePing bool,
	pingTimeout, pingInterval time.Duration,
	writeTimeout, readTimeout time.Duration,
	tpExtractor token.TokenPayloadExtractor) *WsEventHandler {

	hh := &WsEventHandler{
		enablePing:   enablePing,
		pingTimeout:  pingTimeout, // and pong!
		pingInterval: pingInterval,

		writeTimeout: writeTimeout,
		readTimeout:  readTimeout,

		tpExtractor: tpExtractor,
		clients:     sync.Map{},
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

// type Event interface {
// 	OnOpen(socket *Conn)
// 	OnClose(socket *Conn, err error)
// 	OnPing(socket *Conn, payload []byte)
// 	OnPong(socket *Conn, payload []byte)
// 	OnMessage(socket *Conn, message *Message)
// }

func (w *WsEventHandler) OnOpen(socket *gws.Conn) {
	ss := socket.Session()
	userId, exist := ss.Load(contextUserId)
	if !exist {
		logger.Error("user id not exists")
	}

	logger.Info("connection opened (addr: %v, userId: %v)",
		socket.RemoteAddr(), userId)

	// ***

	c := NewEmptyClient()
	c.socket = socket
	w.clients.Store(socket, c)

	if w.enablePing {
		c.pingContext, c.pingCancel =
			context.WithCancel(context.Background())

		go func() {
			for {
				select {
				case <-c.pingContext.Done():
					return

				case <-time.After(w.pingInterval):
					_ = socket.SetDeadline(time.Now().Add(w.pingTimeout))
					_ = socket.WritePing(nil)
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
		rawValue, loaded := w.clients.LoadAndDelete(socket)
		if !loaded { // impossible!?
			return
		}

		client := rawValue.(*Client)
		client.pingCancel()
	} else {
		w.clients.Delete(socket)
	}
}

func (w *WsEventHandler) OnPing(socket *gws.Conn, payload []byte) {
	_ = socket.SetDeadline(time.Now().Add(w.pingTimeout))
	_ = socket.WritePong(nil)
}

func (c *WsEventHandler) OnPong(socket *gws.Conn, payload []byte) {}

// to message handler!
// -----------------------------------------------------------------------

func (c *WsEventHandler) OnMessage(socket *gws.Conn, message *gws.Message) {
	clientMessage := clientSide.Message{}
	if err := json.Unmarshal(message.Bytes(), &clientMessage); err != nil {
		// send errorot to client!
		return
	}

	// ***

	rawValue, ok := c.clients.Load(socket)
	if !ok {
		// disck!!!
		return
	}

	client := rawValue.(*Client)
	if err := client.assertUserIdentity(clientMessage); err != nil {

		return
	}

	// ***

	ph, ok := c.router[clientMessage.Action]
	if !ok {

		return
	}

	ph(client, clientMessage.Payload)
}
