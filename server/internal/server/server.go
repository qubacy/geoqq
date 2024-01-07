package server

import (
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

type Server struct {
	httpServer *http.Server
}

type Dependencies struct {
	Engine *gin.Engine
}

func NewServer(deps Dependencies) (*Server, error) {
	return &Server{
		httpServer: &http.Server{
			Addr:           ":57000",
			MaxHeaderBytes: 1 << 20,
			ReadTimeout:    10 * time.Second,
			WriteTimeout:   10 * time.Second,
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
}
