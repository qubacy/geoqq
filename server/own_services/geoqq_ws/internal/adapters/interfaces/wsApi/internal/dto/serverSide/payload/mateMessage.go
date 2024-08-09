package payload

import (
	domain "common/pkg/domain/geoqq"
	"geoqq_ws/internal/constErrors"
	"reflect"
)

type MateMessage struct {
	Id     float64 `json:"id"`
	ChatId float64 `json:"chat-id"`
	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"`
}

func MateMessageFromDomain(dm *domain.MateMessageWithChat) (*MateMessage, error) {
	if dm == nil {
		typeName := reflect.TypeOf(dm)
		return nil, constErrors.ErrInputParamWithTypeNotSpecified(
			typeName.Name())
	}

	return &MateMessage{
		Id:     float64(dm.Id),
		ChatId: float64(dm.ChatId),
		Text:   dm.Text,
		Time:   float64(dm.Time.Unix()),
		UserId: float64(dm.UserId),
	}, nil
}
