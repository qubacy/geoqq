package api

import "github.com/gin-gonic/gin"

func (h *Handler) userIdentity(ctx *gin.Context) {
	ctx.Set("userId", "value")

	// TODO: check token!
}

/// TODO: ADD DETAILS TO ERR
