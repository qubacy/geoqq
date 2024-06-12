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
	PingContext context.Context
	PingCancel  context.CancelFunc

	socket      *gws.Conn
	tpExtractor token.TokenPayloadExtractor
	UserId      uint64

	KnownLocation bool
	Location      UserLocation
}

func NewEmptyClient() *Client {
	return &Client{}
}
