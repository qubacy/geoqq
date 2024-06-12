package internal

import (
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide/payload"
)

type PayloadHandler = func(*Client, any)

// concrete
// -----------------------------------------------------------------------

func (c *WsEventHandler) updateUserLocation(client *Client, rawPayload any) {
	_, err := payload.NewFromAny[payload.UserLocation](rawPayload)
	if err != nil {
		return
	}
}

func (c *WsEventHandler) addGeoMessage(client *Client, rawPayload any) {
	_, err := payload.NewFromAny[payload.GeoMessage](rawPayload)
	if err != nil {
		client.socket.WriteString("")
		return
	}
}

func (c *WsEventHandler) addMateMessage(client *Client, rawPayload any) {
	_, err := payload.NewFromAny[payload.MateMessage](rawPayload)
	if err != nil {
		client.socket.WriteString("")
		return
	}
}
