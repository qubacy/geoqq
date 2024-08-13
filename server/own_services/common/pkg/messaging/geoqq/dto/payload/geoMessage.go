package payload

import (
	domain "common/pkg/domain/geoqq"
	"time"
)

type GeoMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`

	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
}

func (gm *GeoMessage) ToDomain() *domain.GeoMessage {

	// some data is not translated into...

	return &domain.GeoMessage{
		Id:     uint64(gm.Id),
		Text:   gm.Text,
		Time:   time.Unix(int64(gm.Time), 0),
		UserId: uint64(gm.UserId),
	}
}
