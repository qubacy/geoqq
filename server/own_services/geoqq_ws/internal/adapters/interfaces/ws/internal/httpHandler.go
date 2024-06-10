package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/httpErrorResponse"
	utl "common/pkg/utility"
	"geoqq_ws/internal/adapters/interfaces/ws"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/lxzan/gws"
)

func NewHttpHandler(p *ws.Params) (http.Handler, error) {
	h := NewWsEventHandler(
		p.PingTimeout,
		p.WriteTimeout,
		p.ReadTimeout)

	upgrader := gws.NewUpgrader(h, &gws.ServerOption{
		ParallelEnabled: true,
		Recovery:        gws.Recovery,
	})

	router := gin.Default()
	router.GET("/api/ws", func(ctx *gin.Context) {
		socket, err := upgrader.Upgrade(ctx.Writer, ctx.Request)
		if err != nil {
			httpErrorResponse.ResWithErr(ctx, http.StatusInternalServerError,
				ec.ServerError, utl.NewFuncError(NewHttpHandler, err))
			return
		}

		// ***

		go func() {
			socket.ReadLoop()
		}()
	})

	return router, nil
}

func userIdentityByHeader(ctx *gin.Context) {

}
