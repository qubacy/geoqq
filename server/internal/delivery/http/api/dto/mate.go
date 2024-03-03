package dto

import (
	"geoqq/internal/service/dto"
)

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

const GetParameterCount = "count"
const GetParameterOffset = "offset"

type MateRequestsRes struct {
	Requests []MateRequest `json:"requests"`
}

func MakeRequestsResFromOutput(outputMateRequests *dto.MateRequestsForUserOut) (MateRequestsRes, error) {
	if outputMateRequests == nil {
		return MateRequestsRes{}, ErrInputParameterIsNil
	}

	result := MateRequestsRes{
		Requests: make([]MateRequest, 0,
			len(outputMateRequests.MateRequests)), // reserve?
	}
	mateRequests := outputMateRequests.MateRequests
	for i := range mateRequests {
		mateRequest, err := MakeMateRequestFromOutput(mateRequests[i])
		if err != nil {
			return MateRequestsRes{}, err
		}

		result.Requests = append(
			result.Requests, mateRequest)
	}

	return result, nil
}

type MateRequest struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
}

func MakeMateRequestFromOutput(outputMateRequest *dto.MateRequest) (MateRequest, error) {
	if outputMateRequest == nil {
		return MateRequest{}, ErrInputParameterIsNil
	}

	return MateRequest{
		Id:     float64(outputMateRequest.Id),
		UserId: float64(outputMateRequest.SourceUserId),
	}, nil
}

// GET /api/mate/request/count
// -----------------------------------------------------------------------

type MateRequestCountRes struct {
	Count float64 `json:"count"`
}

// POST /api/mate/request
// -----------------------------------------------------------------------

type MateRequestPostReq struct {
	AccessToken string  `json:"access-token" binding:"required"` // ?
	UserId      float64 `json:"user-id" binding:"required"`
}
