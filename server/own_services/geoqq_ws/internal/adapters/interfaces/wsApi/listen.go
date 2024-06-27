package wsApi

import (
	"common/pkg/token"
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal"
	"geoqq_ws/internal/application/ports/input"
	"net/http"
	"time"
)

type Params struct {
	Host string
	Port uint16

	MaxHeaderKb int

	EnablePing  bool
	PingTimeout time.Duration

	WriteTimeout  time.Duration
	ReadTimeout   time.Duration
	HandleTimeout time.Duration

	TpExtractor token.TokenPayloadExtractor

	UserUc        input.UserUsecase
	OnlineUsersUc input.OnlineUsersUsecase
	MateMessageUc input.MateMessageUsecase
	GeoMessageUc  input.GeoMessageUsecase
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
		EnablePing:  params.EnablePing,
		PingTimeout: params.PingTimeout,

		ReadTimeout:   params.ReadTimeout,
		WriteTimeout:  params.WriteTimeout,
		HandleTimeout: params.HandleTimeout,

		TpExtractor: params.TpExtractor,

		UserUc:        params.UserUc,
		OnlineUsersUc: params.OnlineUsersUc,
		MateMessageUc: params.MateMessageUc,
		GeoMessageUc:  params.GeoMessageUc,
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
	if err := s.httpServer.Shutdown(ctx); err != nil {
		return utl.NewFuncError(s.Stop, err)
	}
	return nil
}
