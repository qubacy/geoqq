package ws

import (
	"net/http"

	"github.com/lxzan/gws"
)

type Params struct {
	Host string
	Port uint16
}

func Listen(p *Params) {
	upgrader := gws.NewUpgrader(&Handler{}, &gws.ServerOption{
		ParallelEnabled: true,
		Recovery:        gws.Recovery,
	})

	http.HandleFunc("/api/ws", func(writer http.ResponseWriter, request *http.Request) {
		socket, err := upgrader.Upgrade(writer, request)
		if err != nil {
			return
		}
		go func() {
			socket.ReadLoop() // Blocking prevents the context from being GC.
		}()
	})

	http.ListenAndServe(":6666", nil)
}
