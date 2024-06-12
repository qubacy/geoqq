package internal

import (
	"common/pkg/logger"
	"common/pkg/token"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/clientSide"
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
}

func NewWsEventHandler(pingTimeout,
	writeTimeout, readTimeout time.Duration,
	tpExtractor token.TokenPayloadExtractor) *WsEventHandler {

	return &WsEventHandler{
		pingTimeout:  pingTimeout, // and pong!
		writeTimeout: writeTimeout,
		readTimeout:  readTimeout,
		clients:      sync.Map{},
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
	logger.Info("connection opened (addr: %v)", socket.RemoteAddr())

	c := NewEmptyClient()
	c.Socket = socket
	w.clients.Store(socket, c)

	if w.enablePing {
		c.PingContext, c.PingCancel =
			context.WithCancel(context.Background())

		go func() {
			for {
				select {
				case <-c.PingContext.Done():
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
		client.PingCancel()
	} else {
		w.clients.Delete(socket)
	}
}

func (w *WsEventHandler) OnPing(socket *gws.Conn, payload []byte) {
	_ = socket.SetDeadline(time.Now().Add(w.pingTimeout))
	_ = socket.WritePong(nil)
}

func (c *WsEventHandler) OnPong(socket *gws.Conn, payload []byte) {}

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
	if err := client.identify(clientMessage); err != nil {
		return
	}

	// use middleware!

	// to next handler!

}
