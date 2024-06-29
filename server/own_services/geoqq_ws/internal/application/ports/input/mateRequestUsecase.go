package input

import (
	"context"
)

type UserIdWithMateRequest struct {
	TargetUserId  uint64
	SourceUserId  uint64
	MateRequestId uint64
}

type MateRequestUsecase interface {
	ForwardMateRequest(ctx context.Context, sourceUserId, targetUserId, requestId uint64) error
	GetFbChansForGeoMessages() []<-chan UserIdWithMateRequest
}
