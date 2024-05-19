package dto

import (
	"geoqq_http/internal/domain"
	"geoqq_http/internal/service/dto"
	utl "geoqq_http/pkg/utility"
)

// GET /api/mate/chat
// -----------------------------------------------------------------------

type MateChatsRes struct {
	Chats []*MateChat `json:"chats"`
}

func NewMateChatsResFromOutput(outputMateChats domain.MateChatList) (
	*MateChatsRes, error,
) {
	if outputMateChats == nil {
		return nil, ErrNilInputParameterWithName("MateChatList")
	}

	mateChats := make([]*MateChat, 0, len(outputMateChats))
	for i := range outputMateChats {
		mateChat, err := NewMateChatFromOutput(outputMateChats[i])
		if err != nil {
			return nil, utl.NewFuncError(NewMateChatsResFromOutput, err)
		}

		mateChats = append(mateChats, mateChat)
	}

	return &MateChatsRes{
		Chats: mateChats,
	}, nil
}

type MateChat struct {
	Id     float64 `json:"id"`
	UserId float32 `json:"user-id"`

	NewMessageCount float64      `json:"new-message-count"`
	LastMessage     *MateMessage `json:"last-message"`
}

func NewMateChatFromOutput(outputMateChat *domain.MateChat) (*MateChat, error) {
	if outputMateChat == nil {
		return nil, ErrNilInputParameterWithName("MateChat")
	}

	mateChat := MateChat{
		Id:              float64(outputMateChat.Id),
		UserId:          float32(outputMateChat.UserId),
		NewMessageCount: float64(outputMateChat.NewMessageCount),
		LastMessage:     nil,
	}

	if outputMateChat.LastMessage != nil {
		mateMessage, err := NewMateMessageFromDomain(outputMateChat.LastMessage)
		if err != nil {
			return nil, utl.NewFuncError(NewMateChatFromOutput, err)
		}

		mateChat.LastMessage = mateMessage
	}

	return &mateChat, nil
}

type MateMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
	Read   bool    `json:"read"`
}

func NewMateMessageFromDomain(mateMessage *domain.MateMessage) (
	*MateMessage, error,
) {
	if mateMessage == nil {
		return nil, ErrNilInputParameter
	}

	return &MateMessage{
		Id:     float64(mateMessage.Id),
		Text:   mateMessage.Text,
		Time:   float64(mateMessage.Time.Unix()), // utc ---> unix or not?
		UserId: float64(mateMessage.UserId),
		Read:   mateMessage.Read,
	}, nil
}

// POST /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

type MateChatMessagePostReq struct {
	Text string `json:"text" binding:"required"`
}

// GET /api/mate/chat/{id}/message
// -----------------------------------------------------------------------

type MessagesFromMateChatWithIdRes struct {
	MateMessages []*MateMessage `json:"messages"`
}

func NewMateChatMessagesResFromDomain(domainMateMessages domain.MateMessageList) (
	*MessagesFromMateChatWithIdRes, error,
) {
	sourceFunc := NewMateChatMessagesResFromDomain
	if domainMateMessages == nil {
		return nil, ErrNilInputParameterWithName("MateMessageList")
	}

	mateMessages := make([]*MateMessage, 0, len(domainMateMessages))
	for i := range domainMateMessages {
		mateMessage, err := NewMateMessageFromDomain(domainMateMessages[i])
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		mateMessages = append(mateMessages, mateMessage)
	}

	return &MessagesFromMateChatWithIdRes{
		MateMessages: mateMessages,
	}, nil
}

// GET /api/mate/request
// -----------------------------------------------------------------------

type MateRequestsRes struct {
	Requests []*MateRequest `json:"requests"`
}

func NewRequestsResFromOutput(outputMateRequests *dto.MateRequestsForUserOut) (
	*MateRequestsRes, error,
) {
	if outputMateRequests == nil {
		return nil, ErrNilInputParameterWithName("MateRequestsForUserOut")
	}

	requests := make([]*MateRequest, 0,
		len(outputMateRequests.MateRequests))

	mateRequests := outputMateRequests.MateRequests
	for i := range mateRequests {
		mateRequest, err := NewMateRequestFromOutput(mateRequests[i])
		if err != nil {
			return nil, utl.NewFuncError(NewRequestsResFromOutput, err)
		}

		requests = append(requests, mateRequest)
	}

	return &MateRequestsRes{
		Requests: requests,
	}, nil
}

type MateRequest struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
}

func NewMateRequestFromOutput(outputMateRequest *dto.MateRequest) (*MateRequest, error) {
	if outputMateRequest == nil {
		return nil, ErrNilInputParameter
	}

	return &MateRequest{
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
	UserId float64 `json:"user-id" binding:"required"`
}
