package http

import (
	"errors"
	"geoqq/internal/delivery/http/api"
	"geoqq/internal/service"
	"geoqq/pkg/token"
	"net/http"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	tokenExtractor token.TokenExtractor
	services       service.Services // <--- all services

	engine     *gin.Engine // <--- all other routes
	apiHandler *api.Handler
	//...
}

type Dependencies struct {
	Services       service.Services
	TokenExtractor token.TokenExtractor
}

func (d *Dependencies) validate() error {
	if d.TokenExtractor == nil {
		return errors.New("token extractor is nil")
	}
	if d.Services == nil {
		return errors.New("services is nil")
	}
	return nil
}

// ctor
// -----------------------------------------------------------------------

func NewHandler(deps Dependencies) (*Handler, error) {
	if err := deps.validate(); err != nil {
		return nil, err
	}

	// ***

	var engine *gin.Engine = gin.Default()

	// TODO: add extra middlewares!

	// TODO: remove debug route
	engine.GET("/ping", func(ctx *gin.Context) {
		ctx.String(http.StatusOK, "pong")
	})

	// ***

	handler := &Handler{
		services:       deps.Services,
		tokenExtractor: deps.TokenExtractor,

		engine: engine,
	}
	err := handler.initApi()
	if err != nil {
		return nil, err
	}

	return handler, nil
}

// private
// -----------------------------------------------------------------------

func (h *Handler) initApi() error {
	deps := api.Dependencies{
		Services:       h.services,
		Router:         h.engine.Group("/api"),
		TokenExtractor: h.tokenExtractor,
	}

	apiHandler, err := api.NewHandler(deps)
	if err != nil {
		return err
	}

	h.apiHandler = apiHandler
	return nil
}

// unsafe public
// -----------------------------------------------------------------------

func (h *Handler) GetEngine() *gin.Engine {
	return h.engine
}
