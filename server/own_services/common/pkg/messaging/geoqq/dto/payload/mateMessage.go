package payload

import (
	domain "common/pkg/domain/geoqq"
	"time"
)

type MateMessage struct {
	TargetUserId float64 `json:"target-user-id"`

	Id     float64 `json:"id"`
	ChatId float64 `json:"chat-id"`

	Text   string  `json:"text"`
	Time   float64 `json:"time"`
	UserId float64 `json:"user-id"` // from!
	Read   bool    `json:"read"`    // not used?
}

func (mm *MateMessage) ToDomain() *domain.MateMessageWithChat {
	return &domain.MateMessageWithChat{
		Id:     uint64(mm.Id),
		ChatId: uint64(mm.ChatId),
		Text:   mm.Text,
		Time:   time.Unix(int64(mm.Time), 0), // !
		UserId: uint64(mm.UserId),
		Read:   mm.Read,
	}
}
