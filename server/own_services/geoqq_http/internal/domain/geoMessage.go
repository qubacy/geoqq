package domain

import "time"

type GeoMessage struct {
	Id     uint64
	UserId uint64
	Text   string
	Time   time.Time
}

func NewGeoMessageWithNowTime(id, userId uint64, text string) *GeoMessage {
	return &GeoMessage{
		Id:     id,
		UserId: userId,
		Text:   text,
		Time:   time.Now().UTC(),
	}
}

type GeoMessageList []*GeoMessage
