package dto

// GET /api/mate/chat
// -----------------------------------------------------------------------

type MateChatsRes struct {
	Chats []MateChat `json:"chats"`
}

type MateChat struct {
	Id     float64 `json:"id"`
	UserId float32 `json:"user-id"`

	NewMessageCount float64     `json:"new-message-count"`
	LastMessage     MateMessage `json:"last-message"`
}

type MateMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
}

// GET /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

type MessagesFromMateChatWithIdRes struct {
	MateMessage []MateMessage `json:"messages"`
}

// GET /api/mate/request
// -----------------------------------------------------------------------

type MateRequestsRes struct {
	Requests []MateRequest `json:"requests"`
}

type MateRequest struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
}

// GET /api/mate/request/count
// -----------------------------------------------------------------------

type MateRequestCountRes struct {
	Count float64 `json:"count"`
}

// POST /api/mate/request
// -----------------------------------------------------------------------

type MateRequestPostReq struct {
	AccessToken string  `json:"access-token"` // ?
	UserId      float64 `json:"user-id"`
}
