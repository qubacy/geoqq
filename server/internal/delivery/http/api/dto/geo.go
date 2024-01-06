package dto

// GET /api/geo/chat/message
// -----------------------------------------------------------------------

type GeoChatMessagesRes struct {
	Messages []GeoMessage `json:"messages"`
}

type GeoMessage struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
}
