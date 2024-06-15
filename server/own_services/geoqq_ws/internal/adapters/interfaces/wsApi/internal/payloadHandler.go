package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/utility"
	"context"
	cside "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide/payload"
	sside "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	inputDto "geoqq_ws/internal/application/ports/input/dto"
)

type PayloadHandler = func(*Client, any)

// concrete
// -----------------------------------------------------------------------

func (w *WsEventHandler) updateUserLocation(client *Client, rawPayload any) {
	sourceFunc := w.updateUserLocation
	eventFailedName := sside.MakeEventWithPostfix(
		cside.ActionUpdateUserLocation, sside.PostfixFailed)

	// ***

	payload, err := payload.NewFromAny[payload.UserLocation](rawPayload)
	if err != nil {
		w.resWithClientError(client.socket, eventFailedName,
			ec.Parse_JsonPayloadFailed, utility.NewFuncError(sourceFunc, err))
		return
	}

	err = w.userUc.UpdateUserLocation(context.TODO(), inputDto.UpdateUserLocation{
		UserId:    client.userId,
		Longitude: payload.Longitude,
		Latitude:  payload.Latitude,
		Radius:    uint64(payload.Radius),
	})

	// ***

	if err != nil {
		w.resWithErrorForClient(client.socket, eventFailedName, err)
	} else {
		eventSucceededName := sside.MakeEventWithPostfix(
			cside.ActionUpdateUserLocation, sside.PostfixSucceeded)
		w.resWithOK(client.socket, eventSucceededName)
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
