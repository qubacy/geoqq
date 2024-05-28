package domain

import "time"

type MateMessage struct {
	Id     uint64
	Text   string
	Time   time.Time
	UserId uint64
	Read   bool
}

// such a constructor needed?
func NewEmptyMateMessage() *MateMessage {
	return &MateMessage{
		0, "", time.Time{}, 0, false,
	}
}

func NewMessageWithNowTime(id, userId uint64, text string) *MateMessage {
	return &MateMessage{
		Id:     id,
		Text:   text,
		Time:   time.Now().UTC(), // small inaccuracy!
		UserId: userId,
		Read:   false,
	}
}

type MateMessageList []*MateMessage
