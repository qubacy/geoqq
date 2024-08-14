package payload

import (
	domain "common/pkg/domain/geoqq"
	utl "common/pkg/utility"
	"geoqq_ws/internal/constErrors"
)

/*

{
	"id": "<id>",
	"user-id": "<id>",
	"new-message-count": "<int>",
	"last-message": {
		"id": "<id>",
		"text": "<string>",
		"time": "<int>",
		"user-id": "<id>"
	},
	"last-action-time": "<int>"
}

*/

type MateChat struct {
	Id              float64      `json:"id"`
	UserId          float64      `json:"user-id"`
	NewMessageCount float64      `json:"new-message-count"`
	LastMessage     *MateMessage `json:"last-message,omitempty"`
	LastActionTime  float64      `json:"last-action-time"`
}

func MateChatFromDomain(dm *domain.MateChat) (*MateChat, error) {
	if dm == nil {
		return nil, constErrors.ErrInputParamWithTypeNotSpecified(
			utl.GetTypeName(dm))
	}

	return &MateChat{
		Id:              float64(dm.Id),
		UserId:          float64(dm.UserId),
		NewMessageCount: float64(dm.NewMessageCount),
		LastMessage:     MateMessageFromDomainOrNil(dm.LastMessage),
		LastActionTime:  float64(dm.LastActionTime.Unix()), // !
	}, nil
}
