package domain

import (
	"time"
)

type MateMessage struct {
	Id     uint64
	ChatId uint64
	Text   string
	Time   time.Time
	UserId uint64
	Read   bool
}
