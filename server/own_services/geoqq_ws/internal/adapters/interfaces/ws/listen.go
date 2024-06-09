package ws

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"fmt"
	"geoqq_ws/internal/adapters/interfaces/ws/internal"
	"net/http"
	"time"

	"github.com/lxzan/gws"
)

type Params struct {
	Host string
	Port uint16

	PingTimeout  time.Duration
	WriteTimeout time.Duration
	ReadTimeout  time.Duration
}

func (p *Params) createAddr() string {
	return fmt.Sprintf("%v:%v", p.Host, p.Port)
}

// public
// -----------------------------------------------------------------------

func Listen(p *Params) error {
	h := internal.NewHandler(p.PingTimeout,
		p.WriteTimeout,
		p.ReadTimeout)

	upgrader := gws.NewUpgrader(h, &gws.ServerOption{
		ParallelEnabled: true,
		Recovery:        gws.Recovery,
	})

	http.HandleFunc("/api/ws", func(writer http.ResponseWriter, request *http.Request) {
		if request.Method != http.MethodGet {
			http.NotFound(writer, request)
			return
		}

		request.ParseForm() // TODO: !!!

		socket, err := upgrader.Upgrade(writer, request)
		if err != nil {
			logger.Error("%v", utl.NewFuncError(Listen, err))
			return
		}

		go func() {
			socket.ReadLoop()
		}()
	})

	if err := http.ListenAndServe(p.createAddr(), nil); err != nil {
		return utl.NewFuncError(Listen, err)
	}
	return nil
}
