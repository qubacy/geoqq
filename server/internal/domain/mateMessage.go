package domain

import "time"

type MateMessage struct {
	Id     uint64
	Text   string
	Time   time.Time
	UserId uint64
}

type MateMessageList []MateMessage
