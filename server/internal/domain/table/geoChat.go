package table

import "time"

type GeoMessage struct {
	Id         uint64
	FromUserId uint64
	Text       string
	Time       time.Time
	Longitude  float64
	Latitude   float64
}
