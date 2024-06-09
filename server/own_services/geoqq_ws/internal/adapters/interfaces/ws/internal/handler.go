package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/clientSide"
	"time"

	"github.com/lxzan/gws"
)

type Handler struct {
	pingTimeout  time.Duration
	writeTimeout time.Duration
	readTimeout  time.Duration

	clients map[*gws.Conn]Client
}

func NewHandler(pingTimeout, writeTimeout, readTimeout time.Duration) *Handler {
	return &Handler{
		pingTimeout:  pingTimeout,
		writeTimeout: writeTimeout,
		readTimeout:  readTimeout,

		clients: make(map[*gws.Conn]Client),
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

func (c *Handler) OnOpen(socket *gws.Conn) {
	logger.Info("connection opened (addr: %v)", socket.LocalAddr())
	c.clients[socket] = MakeEmptyClient()
}

func (c *Handler) OnClose(socket *gws.Conn, err error) {
	logger.Warning("%v", utl.NewFuncError(c.OnClose, err))
	delete(c.clients, socket)
}

func (c *Handler) OnPing(socket *gws.Conn, payload []byte) {

	_ = socket.SetDeadline(time.Now().Add(c.pingTimeout))
	_ = socket.WritePong(nil)
}

func (c *Handler) OnPong(socket *gws.Conn, payload []byte) {}

// -----------------------------------------------------------------------

func (c *Handler) OnMessage(socket *gws.Conn, message *gws.Message) {
	clientMessage := clientSide.Message{}
	if err := json.Unmarshal(message.Bytes(), &clientMessage); err != nil {
		c.resWithClientError(socket, ec.ParseRequestJsonBodyFailed,
			utl.NewFuncError(c.OnMessage, err))
		return
	}

	// ***

	// use middleware!

	// to next handler!

}
