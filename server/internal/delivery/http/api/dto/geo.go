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

// POST /api/geo/chat/message
// -----------------------------------------------------------------------

type GeoChatMessagePostReq struct {
	AccessToken string `json:"access-token" binding:"required"` // ?
	Text        string `json:"text" binding:"required"`

	Longitude float64 `json:"longitude" binding:"required"`
	Latitude  float64 `json:"latitude" binding:"required"`
}
