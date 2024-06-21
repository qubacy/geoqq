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
	eventOk, eventFl := sside.MakeEventsOkAndFl(cside.ActionUpdateUserLocation)

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

	w.commonRes(client.socket, eventOk, eventFl, err)
}

func (w *WsEventHandler) addMateMessage(ctx context.Context, client *Client, rawPayload any) {
	sourceFunc := w.addMateMessage
	eventOk, eventFl := sside.MakeEventsOkAndFl(cside.ActionAddMateMessage)

	payload, err := payload.NewFromAny[payload.MateMessage](rawPayload)
	if err != nil {
		w.resWithClientError(client.socket, eventFl,
			ec.Parse_JsonPayloadFailed, utl.NewFuncError(sourceFunc, err))
		return
	}

	// to service/usecase

	err = w.mateMessageUc.AddMateMessage(ctx, client.userId,
		uint64(payload.ChatId), payload.Text)

	// response to client

	w.commonRes(client.socket, eventOk, eventFl, err)
}

func (w *WsEventHandler) addGeoMessage(ctx context.Context, client *Client, rawPayload any) {
	sourceFunc := w.addGeoMessage
	eventOk, eventFl := sside.MakeEventsOkAndFl(cside.ActionAddGeoMessage)

	payload, err := payload.NewFromAny[payload.GeoMessage](rawPayload)
	if err != nil {
		w.resWithClientError(client.socket, eventFl,
			ec.Parse_JsonPayloadFailed, utl.NewFuncError(sourceFunc, err))
		return
	}

	// to service/usecase

	err = w.geoMessageUc.AddGeoMessage(ctx, client.userId,
		payload.Text, payload.Longitude, payload.Latitude)

	// response to client

	w.commonRes(client.socket, eventOk, eventFl, err)
}
