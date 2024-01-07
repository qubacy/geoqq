package server

import "net/http"

type Server struct {
	httpApiServer *http.Server
}

func NewServer() (*Server, error) {
	return nil, nil
}
