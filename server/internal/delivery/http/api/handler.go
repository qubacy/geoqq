package api

import (
	"errors"
	"geoqq/internal/service"
	"geoqq/pkg/token"

	"github.com/gin-gonic/gin"
)

// knows nothing about lower levels!?
type Handler struct {
	tokenExtractor token.TokenExtractor
	services       service.Services
	router         gin.IRouter // <--- maybe *gin.RouterGroup
}

type Dependencies struct {
	TokenExtractor token.TokenExtractor
	Services       service.Services
	Router         gin.IRouter
}

func (d *Dependencies) validate() error {
	if d.TokenExtractor == nil {
		return errors.New("Token extractor is nil")
	}
	if d.Services == nil {
		return errors.New("Services is nil")
	}
	if d.Router == nil {
		return errors.New("Router is nil")
	}
	return nil
}

// ctor
// -----------------------------------------------------------------------

func NewHandler(deps Dependencies) (*Handler, error) {
	// TODO: validate deps!

	handler := &Handler{
		tokenExtractor: deps.TokenExtractor,
		services:       deps.Services,
		router:         deps.Router,
	}

	handler.registerAuthRoutes()
	handler.registerMateRoutes()
	handler.registerUserRoutes()
	handler.registerImageRoutes()
	handler.registerGeoRoutes()
	//...

	return handler, nil
}
