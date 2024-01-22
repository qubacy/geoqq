package table

import "time"

type Avatar struct {
	Id                uint64
	GeneratedByServer bool
	Time              time.Time
	Hash              string
}
