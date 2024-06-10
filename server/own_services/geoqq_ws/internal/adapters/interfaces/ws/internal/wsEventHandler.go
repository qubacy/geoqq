package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"encoding/json"
	"geoqq_ws/internal/adapters/interfaces/ws/internal/dto/clientSide"
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
)

func getApiWs(ctx *gin.Context) {
	if request.Method != http.MethodGet {
		http.NotFound(writer, request)
		return
	}

	if err := request.ParseForm(); err != nil {
		resWithErr(writer)
		return
	}
	authValue := request.Header.Get("Authorization")
	authParts := strings.Split(authValue, " ")

	tPayload, err := p.TokenExtractor.Parse(authParts[1])

	// ***

	socket, err := upgrader.Upgrade(writer, request)
	if err != nil {
		logger.Error("%v", utl.NewFuncError(Listen, err))
		return
	}

	session := socket.Session()
	session.Store("userId", tPayload.UserId)

	go func() {
		socket.ReadLoop()
	}()
}

// -----------------------------------------------------------------------

type WsEventHandler struct {
	pingTimeout  time.Duration
	writeTimeout time.Duration
	readTimeout  time.Duration

	clients map[*gws.Conn]Client
}

func NewWsEventHandler(pingTimeout, writeTimeout, readTimeout time.Duration) *WsEventHandler {
	return &WsEventHandler{
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

func (c *WsEventHandler) OnOpen(socket *gws.Conn) {
	logger.Info("connection opened (addr: %v)", socket.LocalAddr())
	c.clients[socket] = MakeEmptyClient()
}

func (c *WsEventHandler) OnClose(socket *gws.Conn, err error) {
	logger.Warning("%v", utl.NewFuncError(c.OnClose, err))
	delete(c.clients, socket)
}

func (c *WsEventHandler) OnPing(socket *gws.Conn, payload []byte) {

	_ = socket.SetDeadline(time.Now().Add(c.pingTimeout))
	_ = socket.WritePong(nil)
}

func (c *WsEventHandler) OnPong(socket *gws.Conn, payload []byte) {}

// -----------------------------------------------------------------------

func (c *WsEventHandler) OnMessage(socket *gws.Conn, message *gws.Message) {
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
