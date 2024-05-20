package http

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/stretchr/testify/assert"
)

func Test_Handler_GetPing(t *testing.T) {
	handler, err := NewHandler(Dependencies{})
	if err != nil {
		t.Error(err)
	}
	router := handler.GetEngine()

	recorder := httptest.NewRecorder()
	request, err := http.NewRequest("GET", "/ping", nil)
	if err != nil {
		t.Error(err)
	}
	router.ServeHTTP(recorder, request)

	// ***

	assert.Equal(t, http.StatusOK, recorder.Code)
	assert.Equal(t, "pong", recorder.Body.String())
}
