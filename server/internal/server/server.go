package server

import (
	"fmt"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

type Server struct {
	httpServer *http.Server
}

type Dependencies struct {
	Engine *gin.Engine

	Host string
	Port uint16

	ReadTimeout  time.Duration
	WriteTimeout time.Duration
}

func NewServer(deps Dependencies) (*Server, error) {
	return &Server{
		httpServer: &http.Server{
			Addr:           fmt.Sprintf("%s:%v", deps.Host, deps.Port),
			MaxHeaderBytes: 1 << 20,
			ReadTimeout:    deps.ReadTimeout,
			WriteTimeout:   deps.WriteTimeout,
			Handler:        deps.Engine,
		},
	}, nil
}

// public
// -----------------------------------------------------------------------

func (s *Server) Start() error {
	return s.httpServer.ListenAndServe()
}

func (s *Server) Stop() {
	// TODO:!!!
}
