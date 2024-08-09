package geoqq

import (
	"time"
)

type GeoMessage struct {
	Id     uint64
	UserId uint64
	Text   string
	Time   time.Time
}

func NewGeoMessage(id uint64, userId uint64, text string) *GeoMessage {
	return &GeoMessage{
		Id:     id,
		UserId: userId,
		Text:   text,
		Time:   time.Now().UTC(),
	}
}
