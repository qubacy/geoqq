package dto

import (
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
	"geoqq/pkg/utility"
)

// GET /api/mate/chat
// -----------------------------------------------------------------------

type MateChatsRes struct {
	Chats []MateChat `json:"chats"`
}

func MakeMateChatsResFromOutput(outputMateChats domain.MateChatList) (MateChatsRes, error) {
	if outputMateChats == nil {
		return MateChatsRes{}, ErrInputParameterIsNil
	}

	responseDto := MateChatsRes{}
	for i := range outputMateChats {
		mateChat, err := MakeMateChatFromOutput(outputMateChats[i])
		if err != nil {
			return MateChatsRes{},
				utility.NewFuncError(MakeMateChatsResFromOutput, err)
		}

		responseDto.Chats = append(
			responseDto.Chats, mateChat)
	}

	return responseDto, nil
}

type MateChat struct {
	Id     float64 `json:"id"`
	UserId float32 `json:"user-id"`

	NewMessageCount float64      `json:"new-message-count"`
	LastMessage     *MateMessage `json:"last-message"`
}

func MakeMateChatFromOutput(outputMateChat *domain.MateChat) (MateChat, error) {
	if outputMateChat == nil {
		return MateChat{}, ErrInputParameterIsNil
	}

	mateChat := MateChat{
		Id:              float64(outputMateChat.Id),
		UserId:          float32(outputMateChat.UserId),
		NewMessageCount: float64(outputMateChat.NewMessageCount),
		LastMessage:     nil,
	}

	if outputMateChat.LastMessage != nil {
		mateMessage, err := MakeMateMessageFromDomain(outputMateChat.LastMessage)
		if err != nil {
			return MateChat{},
				utility.NewFuncError(MakeMateChatFromOutput, err)
		}

		mateChat.LastMessage = &mateMessage
	}

	return mateChat, nil
}

type MateMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
}

func MakeMateMessageFromDomain(mateMessage *domain.MateMessage) (MateMessage, error) {
	if mateMessage == nil {
		return MateMessage{}, ErrInputParameterIsNil
	}

	return MateMessage{
		Id:     float64(mateMessage.Id),
		Text:   mateMessage.Text,
		Time:   float64(mateMessage.Time.Unix()), // utc ---> unix
		UserId: float64(mateMessage.UserId),
	}, nil
}

// POST /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

type MateChatMessagePostReq struct {
	AccessToken string `json:"access-token" binding:"required"` // ?
	Text        string `json:"text" binding:"required"`
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
