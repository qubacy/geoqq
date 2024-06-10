package api

import (
	"common/pkg/token"
	"common/pkg/utility"
	"errors"
	"geoqq_http/internal/service"

	"github.com/gin-gonic/gin"
)

// knows nothing about lower levels!?
type Handler struct {
	tpExtractor token.TokenPayloadExtractor
	services    service.Services
	router      gin.IRouter // <--- maybe *gin.RouterGroup
}

type Dependencies struct {
	TpExtractor token.TokenPayloadExtractor
	Services    service.Services
	Router      gin.IRouter
}

func (d *Dependencies) validate() error {
	if d.TpExtractor == nil {
		return errors.New("token payload extractor is nil")
	}
	if d.Services == nil {
		return errors.New("services is nil")
	}
	if d.Router == nil {
		return errors.New("router is nil")
	}

	return nil
}

// ctor
// -----------------------------------------------------------------------

func NewHandler(deps Dependencies) (*Handler, error) {
	if err := deps.validate(); err != nil {
		return nil, utility.NewFuncError(NewHandler, err)
	}

	handler := &Handler{
		tpExtractor: deps.TpExtractor,
		services:    deps.Services,
		router:      deps.Router,
	}

	handler.registerAuthRoutes()
	handler.registerMateRoutes()
	handler.registerUserRoutes()
	handler.registerImageRoutes()
	handler.registerGeoRoutes()
	//...

	return handler, nil
}
