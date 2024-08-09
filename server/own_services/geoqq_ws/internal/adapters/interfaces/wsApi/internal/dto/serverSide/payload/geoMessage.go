package payload

import (
	domain "common/pkg/domain/geoqq"
	"geoqq_ws/internal/constErrors"
)

type GeoMessage struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
}

func GeoMessageFromDomain(dm *domain.GeoMessage) (*GeoMessage, error) {
	if dm == nil {
		return nil, constErrors.ErrInputParamWithTypeNotSpecified("*domain.GeoMessage")
	}

	return &GeoMessage{
		Id:     float64(dm.Id),
		UserId: float64(dm.UserId),
		Text:   dm.Text,
		Time:   float64(dm.Time.Unix()),
	}, nil
}
