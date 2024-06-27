package table

import (
	"time"
)

type UserEntry struct {
	Id             uint64
	Login          string
	HashPassword   string
	HashUpdToken   string
	SignUpTime     time.Time
	SignInTime     time.Time
	LastActionTime time.Time
}
