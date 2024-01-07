package api

import "github.com/gin-gonic/gin"

func (h *Handler) registerMateRoutes() {
	router := h.router.Group("/mate", h.userIdentity)
	{
		// TODO: what is the priority of these routes?
		chat := router.Group("/chat")
		{
			chat.GET("", h.getMateChats)
			chat.DELETE("/:id", h.deleteMateChat)
			chat.GET("/:id/message", h.getMateChatMessages) // maybe group?
		}

		request := router.Group("/request")
		{
			request.GET("", h.getMateRequests)
			request.GET("/count", h.getMateRequests)
			request.POST("", h.postMateRequest)
			request.PUT("/:id", h.putMateRequest)
		}
	}
}

// chat
// -----------------------------------------------------------------------

func (h *Handler) getMateChats(ctx *gin.Context) {

}

func (h *Handler) deleteMateChat(ctx *gin.Context) {

}

func (h *Handler) getMateChatMessages(ctx *gin.Context) {

}

// request
// -----------------------------------------------------------------------

func (h *Handler) getMateRequests(ctx *gin.Context) {

}

func (h *Handler) getMateRequestCount(ctx *gin.Context) {

}

func (h *Handler) postMateRequest(ctx *gin.Context) {

}

func (h *Handler) putMateRequest(ctx *gin.Context) {

}
