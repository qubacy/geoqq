package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/httpErrorResponse"
	"common/pkg/token"
	utl "common/pkg/utility"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
)

type Params struct {
	PingTimeout  time.Duration
	WriteTimeout time.Duration
	ReadTimeout  time.Duration
	TpExtractor  token.TokenPayloadExtractor
}

func NewHttpHandler(p *Params) (http.Handler, error) {
	h := NewWsEventHandler(
		p.PingTimeout,
		p.WriteTimeout,
		p.ReadTimeout,
		p.TpExtractor,
	)

	upgrader := gws.NewUpgrader(h, &gws.ServerOption{
		ParallelEnabled: true,
		Recovery:        gws.Recovery,
	})

	router := gin.Default()
	router.GET("/api/ws",
		func(ctx *gin.Context) { userIdentityByHeader(ctx, p.TpExtractor) },
		func(ctx *gin.Context) {
			socket, err := upgrader.Upgrade(ctx.Writer, ctx.Request)
			if err != nil {
				httpErrorResponse.ResWithErr(ctx, http.StatusInternalServerError,
					ec.ServerError, utl.NewFuncError(NewHttpHandler, err))
				return
			}

			// ***

			ss := socket.Session()
			ss.Store(contextUserId, ctx.GetString(contextUserId)) // !

			go func() {
				socket.ReadLoop()
			}()
		})

	return router, nil
}
