package usecase

import (
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/input"
	"math/rand"
)

type MateRequestUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	FbChanCount   int
	FbChanSize    int
}

// -----------------------------------------------------------------------

type MateRequestUsecase struct {
	onlineUsersUc          input.OnlineUsersUsecase
	feedbackChsForMateReqs []chan input.UserIdWithMateRequest
}

func NewMateRequestUsecase(params *MateRequestUcParams) *MateRequestUsecase {
	chans := []chan input.UserIdWithMateRequest{}
	for i := 0; i < params.FbChanCount; i++ {
		ch := make(chan input.UserIdWithMateRequest, params.FbChanSize)
		chans = append(chans, ch)
	}

	// ***

	return &MateRequestUsecase{
		onlineUsersUc:          params.OnlineUsersUc,
		feedbackChsForMateReqs: chans,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateRequestUsecase) ForwardMateRequest(ctx context.Context,
	sourceUserId, targetUserId, requestId uint64) error {

	if !m.onlineUsersUc.UserIsOnline(targetUserId) {
		return nil // user is not in app!
	}

	index := rand.Intn(len(m.feedbackChsForMateReqs))
	m.feedbackChsForMateReqs[index] <- input.UserIdWithMateRequest{
		UserId:        targetUserId,
		SourceUserId:  sourceUserId,
		MateRequestId: requestId,
	}

	return nil
}

// -----------------------------------------------------------------------

func (m *MateRequestUsecase) GetFbChansForMateRequest() []<-chan input.UserIdWithMateRequest {
	return utl.ChanToLeftDirected(m.feedbackChsForMateReqs)
}
