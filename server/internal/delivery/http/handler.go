package http

import (
	"geoqq/internal/delivery/http/api"
	"geoqq/internal/service"
	"net/http"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	services service.Services // <--- all services
	engine   *gin.Engine      // <--- all other routes

	apiHandler *api.Handler
	//...
}

type Dependencies struct {
	Services service.Services
}

func NewHandler(deps Dependencies) (*Handler, error) {
	var engine *gin.Engine = gin.Default()
	engine.Use(gin.Recovery()).
		Use(gin.Logger())

	// TODO: add middlewares!

	// TODO: remove debug route
	engine.GET("/ping", func(ctx *gin.Context) {
		ctx.String(http.StatusOK, "pong")
	})

	// ***

	handler := &Handler{
		services: deps.Services,
		engine:   engine,
	}
	err := handler.initApi()
	if err != nil {
		return nil, err
	}

	return handler, nil
}

func (h *Handler) initApi() error {
	deps := api.Dependencies{
		Services: h.services,
		Router:   h.engine.Group("/api"),
	}

	apiHandler, err := api.NewHandler(deps)
	if err != nil {
		return err
	}

	h.apiHandler = apiHandler
	return nil
}

// unsafe
// -----------------------------------------------------------------------

func (h *Handler) GetEngine() *gin.Engine {
	return h.engine
}
