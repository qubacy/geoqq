package domain

import "time"

type GeoMessage struct {
	Id     uint64
	UserId uint64
	Text   string
	Time   time.Time
}
