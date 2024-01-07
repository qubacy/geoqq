package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerGeoRoutes() {
	router := h.router.Group("/geo")
	{
		chat := router.Group("/chat")
		{
			chat.GET("/message", h.getGeoChatMessages) // maybe group?
		}
	}
}

// geo
// -----------------------------------------------------------------------

func (h *Handler) getGeoChatMessages(ctx *gin.Context) {

}
