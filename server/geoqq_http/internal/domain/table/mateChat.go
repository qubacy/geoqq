package table

import "time"

type MateChat struct {
	Id           uint64
	FirstUserId  uint64
	SecondUserId uint64
}

type MateMessage struct {
	Id         uint64
	MateChatId uint64
	FromUserId uint64
	Text       string
	Time       time.Time
	Read       bool
}

type DeletedMateChat struct {
	ChatId uint64
	UserId uint64
}
