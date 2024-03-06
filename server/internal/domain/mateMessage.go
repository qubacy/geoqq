package domain

import "time"

type MateMessage struct {
	Id     uint64
	Text   string
	Time   time.Time
	UserId uint64
}

func NewEmptyMateMessage() *MateMessage {
	return &MateMessage{
		0, "", time.Time{}, 0,
	}
}

type MateMessageList []*MateMessage
