package internal

import (
	"common/pkg/token"
	"context"

	"github.com/lxzan/gws"
)

type UserLocation struct {
	Longitude float64
	Latitude  float64
}

type Client struct {
	pingContext context.Context
	pingCancel  context.CancelFunc

	socket      *gws.Conn
	tpExtractor token.TokenPayloadExtractor
	userId      uint64

	knownLocation bool
	location      UserLocation
}

func NewEmptyClient() *Client {
	return &Client{}
}
