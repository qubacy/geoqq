package msgs

import (
	"common/pkg/messaging/geoqq"
	"context"
	"geoqq_http/internal/domain"
)

const (
	TextMsgsDisabled = geoqq.TextMessagingDisabled
)

const (
	EventUpdatedPublicUser = geoqq.EventUpdatedPublicUser

	EventAddedMateChat   = geoqq.EventAddedMateChat
	EventUpdatedMateChat = geoqq.EventUpdatedMateChat

	EventAddedMateRequest = geoqq.EventAddedMateRequest
	EventAddedMateMessage = geoqq.EventAddedMateMessage

	EventAddedGeoMessage = geoqq.EventAddedGeoMessage
)

// -----------------------------------------------------------------------

type Msgs interface {
	SendPublicUserId(ctx context.Context, event string, userId uint64) error
	SendMateChatId(ctx context.Context, event string, targetUserId, chatId uint64) error

	SendMateRequest(ctx context.Context, event string,
		targetUserId, requestId, requesterUserId uint64) error

	SendMateMessage(ctx context.Context, event string,
		targetUserId uint64, chatId uint64, domainMm *domain.MateMessage) error
	SendGeoMessage(ctx context.Context, event string,
		lat, lon float64, domainGm *domain.GeoMessage) error
}
