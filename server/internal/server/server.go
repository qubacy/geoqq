package server

import (
	"context"
	"fmt"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

type Server struct {
	httpServer      *http.Server
	shutdownTimeout time.Duration
}

type Dependencies struct {
	Engine *gin.Engine

	Host string
	Port uint16

	ReadTimeout     time.Duration
	WriteTimeout    time.Duration
	ShutdownTimeout time.Duration
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

func (s *Server) Stop() error {
	ctx, cancel := context.WithTimeout(
		context.Background(),
		s.shutdownTimeout,
	)
	defer cancel()

	// ***

	return s.httpServer.Shutdown(ctx)
}
