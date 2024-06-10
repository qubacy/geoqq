package ws

import (
	"common/pkg/token"
	utl "common/pkg/utility"
	"fmt"
	"geoqq_ws/internal/adapters/interfaces/ws/internal"
	"net/http"
	"time"
)

type Params struct {
	Host string
	Port uint16

	MaxHeaderKb int

	PingTimeout  time.Duration
	WriteTimeout time.Duration
	ReadTimeout  time.Duration

	TokenExtractor token.TokenExtractor
}

func (p *Params) createAddr() string {
	return fmt.Sprintf("%v:%v", p.Host, p.Port)
}

type Server struct {
	httpServer *http.Server
}

func New(params *Params) (*Server, error) {
	handler, err := internal.NewHttpHandler(params)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	svr := http.Server{
		Addr:           params.createAddr(),
		MaxHeaderBytes: params.MaxHeaderKb,
		WriteTimeout:   params.WriteTimeout,
		ReadTimeout:    params.ReadTimeout,
		Handler:        handler,
	}

	return &Server{
		httpServer: &svr,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (s *Server) Listen() error {
	if err := s.httpServer.ListenAndServe(); err != nil {
		return utl.NewFuncError(s.Listen, err)
	}
	return nil
}
