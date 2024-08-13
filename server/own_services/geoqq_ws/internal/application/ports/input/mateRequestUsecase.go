package input

import (
	"context"
)

type UserIdWithMateRequest struct {
	UserIdWithEvent
	SourceUserId  uint64
	MateRequestId uint64
}

// -----------------------------------------------------------------------

type MateRequestUsecase interface {
	ForwardMateRequest(ctx context.Context, sourceUserId, targetUserId, requestId uint64) error
	GetFbChansForMateRequest() []<-chan UserIdWithMateRequest
}
