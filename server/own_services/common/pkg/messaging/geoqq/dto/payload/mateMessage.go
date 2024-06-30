package payload

type MateMessage struct {
	TargetUserId float64 `json:"target-user-id"`

	Id     float64 `json:"id"`
	ChatId float64 `json:"chat-id"`

	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"` // from!
	Read   bool    `json:"read"`    // not used?
}
