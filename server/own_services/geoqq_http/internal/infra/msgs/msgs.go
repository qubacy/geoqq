package msgs

import (
	"context"
	"geoqq_http/internal/domain"
)

const (
	EventUpdatedPublicUser = "updated_public_user"

	EventAddedMateChat   = "added_mate_chat"
	EventUpdatedMateChat = "updated_mate_chat"

	EventAddedMateRequest = "added_mate_request"
	EventAddedMateMessage = "added_mate_message"

	EventAddedGeoMessage = "added_geo_message"
)

type Msgs interface {
	SendPublicUser(ctx context.Context, event string, userId uint64) error

	SendMateChat(ctx context.Context, event string,
		targetUserId uint64, mc *domain.MateChat) error
	SendMateRequest(ctx context.Context, event string,
		targetUserId, requestId, requesterUserId uint64) error

	SendMateMessage(ctx context.Context, event string,
		targetUserId uint64, chatId uint64, mm *domain.MateMessage) error
	SendGeoMessage(ctx context.Context, event string,
		lat, lon float64, gm *domain.GeoMessage) error
}
