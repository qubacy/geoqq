package dto

type MateChat struct {
	TargetUserId *uint64 `json:"target-user-id"`
	Id           float64 `json:"id"`
	UserId       float32 `json:"user-id"`

	NewMessageCount float64      `json:"new-message-count"`
	LastMessage     *MateMessage `json:"last-message"`
}

type MateMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
	Read   bool    `json:"read"`
}
