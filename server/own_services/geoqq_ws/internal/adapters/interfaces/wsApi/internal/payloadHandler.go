package internal

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	cside "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide"
	"geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/clientSide/payload"
	sside "geoqq_ws/internal/adapters/interfaces/wsApi/internal/dto/serverSide"
	inDto "geoqq_ws/internal/application/ports/input/dto"
)

type PayloadHandler = func(context.Context, *Client, any)

// concrete
// -----------------------------------------------------------------------

func (w *WsEventHandler) updateUserLocation(ctx context.Context, client *Client, rawPayload any) {
	sourceFunc := w.updateUserLocation
	actionName := cside.ActionUpdateUserLocation

	eventOk := sside.MakeEventSucceeded(actionName)
	eventFl := sside.MakeEventFailed(actionName)

	payload, err := payload.NewFromAny[payload.UserLocation](rawPayload)
	if err != nil {
		w.resWithClientError(client.socket, eventFl,
			ec.Parse_JsonPayloadFailed, utl.NewFuncError(sourceFunc, err))
		return
	}

	// to service/usecase

	err = w.userUc.UpdateUserLocation(ctx, inDto.UpdateUserLocation{
		UserId:    client.userId,
		Longitude: payload.Longitude,
		Latitude:  payload.Latitude,
		Radius:    uint64(payload.Radius),
	})

	// response to client

	if err != nil {
		w.resWithErrorForClient(client.socket, eventFl, err)
		return
	}

	w.resWithOK(client.socket, eventOk)
}

func (w *WsEventHandler) addGeoMessage(ctx context.Context, client *Client, rawPayload any) {
	// sourceFunc := w.addGeoMessage
	// actionName := cside.ActionAddGeoMessage

	_, err := payload.NewFromAny[payload.GeoMessage](rawPayload)
	if err != nil {
		client.socket.WriteString("")
		return
	}
}

func (c *WsEventHandler) addMateMessage(ctx context.Context, client *Client, rawPayload any) {
	_, err := payload.NewFromAny[payload.MateMessage](rawPayload)
	if err != nil {
		client.socket.WriteString("")
		return
	}
}
