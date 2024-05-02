package dto

import (
	"geoqq/internal/domain"
	"geoqq/pkg/utility"
)

// GET /api/geo/chat/message
// -----------------------------------------------------------------------

type GeoChatMessagesRes struct {
	Messages []GeoMessage `json:"messages"`
}

func MakeGeoChatMessagesResFromDomain(
	domainGeoMessages domain.GeoMessageList) (
	GeoChatMessagesRes, error,
) {
	sourceFunc := MakeGeoChatMessagesResFromDomain
	if domainGeoMessages == nil {
		return GeoChatMessagesRes{},
			ErrInputParameterIsNil
	}

	res := GeoChatMessagesRes{
		Messages: make([]GeoMessage, 0, len(domainGeoMessages)),
	}
	for i := range domainGeoMessages {
		geoMessage, err := MakeGeoMessageFromDomain(
			domainGeoMessages[i],
		)
		if err != nil {
			return GeoChatMessagesRes{},
				utility.NewFuncError(sourceFunc, err)
		}

		res.Messages = append(res.Messages, geoMessage)
	}

	return res, nil
}

type GeoMessage struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
}

func MakeGeoMessageFromDomain(geoMessage *domain.GeoMessage) (GeoMessage, error) {
	if geoMessage == nil {
		return GeoMessage{}, ErrInputParameterIsNil
	}

	return GeoMessage{
		Id:     float64(geoMessage.Id),
		Text:   geoMessage.Text,
		UserId: float64(geoMessage.UserId),
		Time:   float64(geoMessage.Time.Unix()),
	}, nil
}

// POST /api/geo/chat/message
// -----------------------------------------------------------------------

type GeoChatMessagePostReq struct {
	Text string `json:"text" binding:"required"`

	Longitude float64 `json:"longitude" binding:"required"`
	Latitude  float64 `json:"latitude" binding:"required"`
}
