package input

import (
	"context"
)

type UserIdWithMateRequest struct {
	UserId        uint64 // target!
	SourceUserId  uint64
	MateRequestId uint64
}

func (u UserIdWithMateRequest) GetUserId() uint64 {
	return u.UserId
}

// -----------------------------------------------------------------------

type MateRequestUsecase interface {
	ForwardMateRequest(ctx context.Context, sourceUserId, targetUserId, requestId uint64) error
	GetFbChansForMateRequest() []<-chan UserIdWithMateRequest
}
