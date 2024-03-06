package domain

type MateChat struct {
	Id     uint64
	UserId uint64

	NewMessageCount uint64
	LastMessage     *MateMessage
}

func NewEmptyMateChat() *MateChat {
	return &MateChat{
		0, 0, 0, nil,
	}
}

type MateChatList []*MateChat
