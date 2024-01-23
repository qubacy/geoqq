package table

import "time"

// Metadata?

type Avatar struct {
	Id                uint64
	GeneratedByServer bool
	Time              time.Time
	Hash              string
}
