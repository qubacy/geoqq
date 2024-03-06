package domain

type MateChat struct {
	Id     uint64
	UserId uint64

	NewMessageCount uint64
	LastMessage     *MateMessage
}

type MateChatList []MateChat
