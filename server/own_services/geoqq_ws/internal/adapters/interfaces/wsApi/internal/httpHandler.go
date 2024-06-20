package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/token"
	utl "common/pkg/utility"
	"geoqq_ws/internal/application/ports/input"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
)

type Params struct {
	EnablePing   bool
	PingInterval time.Duration
	PingTimeout  time.Duration

	WriteTimeout time.Duration
	ReadTimeout  time.Duration

	TpExtractor token.TokenPayloadExtractor

	UserUc        input.UserUsecase
	MateMessageUc input.MateMessageUsecase
	OnlineUsersUc input.OnlineUsersUsecase
}

// -----------------------------------------------------------------------

func NewHttpHandler(p *Params) (http.Handler, error) {

	h := NewWsEventHandler(p)

	upgrader := gws.NewUpgrader(h, &gws.ServerOption{
		ParallelEnabled: true,
		Recovery:        gws.Recovery,
	})

	engine := gin.Default()
	engine.GET("/ping", func(ctx *gin.Context) {
		ctx.String(http.StatusOK, "pong")
	})
	engine.GET("/api/ws",
		func(ctx *gin.Context) { userIdentityByHeader(ctx, p.TpExtractor) },
		func(ctx *gin.Context) {
			socket, err := upgrader.Upgrade(ctx.Writer, ctx.Request)
			if err != nil {
				err = utl.NewFuncError(NewHttpHandler, err)
				httpResWithServerError(ctx, ec.ServerError, err) // ?
				return
			}

			// ***

			ss := socket.Session()
			ss.Store(contextUserId, ctx.GetUint64(contextUserId)) // !
			ctx.Set(contextUserId, nil)                           // reset...

			go func() {
				socket.ReadLoop() // here websocket!
			}()
		},
		//...
	)
	return engine, nil
}
