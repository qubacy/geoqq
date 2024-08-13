package payload

import (
	domain "common/pkg/domain/geoqq"
	"geoqq_ws/internal/constErrors"
	"reflect"
)

type MateMessageWithChat struct {
	Id     float64 `json:"id"`
	ChatId float64 `json:"chat-id"` // !
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
	Read   bool    `json:"read"`
}

func MateMessageWithChatFromDomain(dm *domain.MateMessageWithChat) (*MateMessageWithChat, error) {
	if dm == nil {
		typeName := reflect.TypeOf(dm)
		return nil, constErrors.ErrInputParamWithTypeNotSpecified(
			typeName.Name())
	}

	return &MateMessageWithChat{
		Id:     float64(dm.Id),
		ChatId: float64(dm.ChatId),
		Text:   dm.Text,
		Time:   float64(dm.Time.Unix()),
		UserId: float64(dm.UserId),
		Read:   dm.Read,
	}, nil
}

// -----------------------------------------------------------------------

type MateMessage struct {
	Id     float64 `json:"id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
	Read   bool    `json:"read"`
}

func MateMessageFromDomainOrNil(dm *domain.MateMessage) *MateMessage {
	if dm == nil {
		return nil
	}

	return &MateMessage{
		Id:     float64(dm.Id),
		Text:   dm.Text,
		Time:   float64(dm.Time.Unix()),
		UserId: float64(dm.UserId),
		Read:   dm.Read,
	}
}
