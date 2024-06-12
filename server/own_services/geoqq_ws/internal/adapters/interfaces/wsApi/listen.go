package wsApi

import (
	"common/pkg/token"
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal"
	"net/http"
	"time"
)

type Params struct {
	Host string
	Port uint16

	MaxHeaderKb int

	EnablePing  bool
	PingTimeout time.Duration

	WriteTimeout time.Duration
	ReadTimeout  time.Duration

	TpExtractor token.TokenPayloadExtractor
}

func (p *Params) createAddr() string {
	return fmt.Sprintf("%v:%v", p.Host, p.Port)
}

// -----------------------------------------------------------------------

type Server struct {
	httpServer *http.Server
}

func New(params *Params) (*Server, error) {
	handler, err := internal.NewHttpHandler(&internal.Params{
		PingTimeout:  params.PingTimeout,
		WriteTimeout: params.WriteTimeout,
		ReadTimeout:  params.ReadTimeout,
		TpExtractor:  params.TpExtractor,
	})
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	// ***

	svr := http.Server{
		Addr:           params.createAddr(),
		MaxHeaderBytes: params.MaxHeaderKb,
		WriteTimeout:   params.WriteTimeout,
		ReadTimeout:    params.ReadTimeout,
		Handler:        handler,
	}

	return &Server{httpServer: &svr}, nil
}

// public
// -----------------------------------------------------------------------

func (s *Server) Listen() error { // to goroutine!

	if err := s.httpServer.ListenAndServe(); err != nil {
		return utl.NewFuncError(s.Listen, err)
	}
	return nil
}

func (s *Server) Stop(ctx context.Context) error {
	return utl.NewFuncError(
		s.Stop, s.httpServer.Shutdown(ctx))
}
