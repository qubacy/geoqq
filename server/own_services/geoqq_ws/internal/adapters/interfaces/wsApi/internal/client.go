package internal

import (
	"common/pkg/token"
	"context"

	"github.com/lxzan/gws"
)

type Client struct {
	pingContext context.Context
	pingCancel  context.CancelFunc

	socket      *gws.Conn
	tpExtractor token.TokenPayloadExtractor
	userId      uint64
}

func NewClient(socket *gws.Conn, tpe token.TokenPayloadExtractor, userId uint64) *Client {
	return &Client{
		pingContext: nil,
		pingCancel:  nil,

		socket:      socket,
		tpExtractor: tpe,
		userId:      userId,
	}
}
