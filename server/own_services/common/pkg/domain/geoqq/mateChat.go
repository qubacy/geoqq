package geoqq

import (
	"time"
)

type MateChat struct {
	Id     uint64
	UserId uint64

	NewMessageCount uint64
	LastMessage     *MateMessage
	LastActionTime  time.Time
}

func NewEmptyMateChat() *MateChat {
	return &MateChat{
		0, 0, 0, nil,
		time.Time{}, // ?
	}
}

type MateChatList []*MateChat
