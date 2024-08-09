package geoqq

import (
	"time"
)

type UserLocation struct {
	UserId uint64
	Lon    float64
	Lat    float64
	Time   time.Time
}
