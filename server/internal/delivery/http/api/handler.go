package api

import (
	"geoqq/internal/service"

	"github.com/gin-gonic/gin"
)

// knows nothing about lower levels!?
type Handler struct {
	services service.Services
	router   gin.IRouter
}

type Dependencies struct {
	Services service.Services
	Router   gin.IRouter // <--- maybe gin.RouterGroup
}

func NewHandler(deps Dependencies) (*Handler, error) {
	handler := &Handler{
		services: deps.Services,
		router:   deps.Router,
	}

	handler.registerAuthRoutes()
	handler.registerMateRoutes()
	//...

	return handler, nil
}
