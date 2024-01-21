package api

import (
	"fmt"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
)

func Test_context_Set_Get(t *testing.T) {
	ctx, _ := gin.CreateTestContext(httptest.NewRecorder())
	ctx.Set(contextUserId, uint64(123))

	userId := ctx.GetUint64(contextUserId)
	if userId != 123 {
		t.Error()
	}
}

func Test_context_GetWithoutSet(t *testing.T) {
	ctx, _ := gin.CreateTestContext(httptest.NewRecorder())

	userId := ctx.GetUint64(contextUserId)
	fmt.Println(userId)
	if userId != 0 {
		t.Error()
	}
}
