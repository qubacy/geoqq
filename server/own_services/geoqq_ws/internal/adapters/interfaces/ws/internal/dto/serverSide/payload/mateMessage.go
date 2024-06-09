package payload

type MateMessage struct {
	Id     float64 `json:"id"`
	ChatId float64 `json:"chat-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
}
