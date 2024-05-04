package dto

import (
	"geoqq/internal/domain"
	utl "geoqq/pkg/utility"
)

// GET /api/geo/chat/message
// -----------------------------------------------------------------------

type GeoChatMessagesRes struct {
	Messages []*GeoMessage `json:"messages"`
}

func NewGeoChatMessagesResFromDomain(
	domainGeoMessages domain.GeoMessageList) (
	*GeoChatMessagesRes, error,
) {
	sourceFunc := NewGeoChatMessagesResFromDomain
	if domainGeoMessages == nil {
		return nil, ErrNilInputParameter
	}

	geoMessages := make([]*GeoMessage, 0, len(domainGeoMessages))
	for i := range domainGeoMessages {
		geoMessage, err := NewGeoMessageFromDomain(
			domainGeoMessages[i],
		)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		geoMessages = append(geoMessages, geoMessage)
	}

	return &GeoChatMessagesRes{
		Messages: geoMessages,
	}, nil
}

type GeoMessage struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
}

func NewGeoMessageFromDomain(geoMessage *domain.GeoMessage) (*GeoMessage, error) {
	if geoMessage == nil {
		return nil, ErrNilInputParameter
	}

	return &GeoMessage{
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
