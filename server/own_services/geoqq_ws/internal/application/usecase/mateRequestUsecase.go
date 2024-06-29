package usecase

import (
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

func New(params *MateRequestUcParams) (*MateRequestUsecase, error) {
	chans := []chan input.UserIdWithMateRequest{}
	for i := 0; i < params.FbChanCount; i++ {
		ch := make(chan input.UserIdWithMateRequest, params.FbChanSize)
		chans = append(chans, ch)
	}

	// ***

	return &MateRequestUsecase{
		onlineUsersUc:          params.OnlineUsersUc,
		feedbackChsForMateReqs: chans,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (m *MateRequestUsecase) ForwardMateRequest(ctx context.Context,
	sourceUserId, targetUserId, requestId uint64) error {

	if !m.onlineUsersUc.UserIsOnline(targetUserId) {
		return nil
	}

	index := rand.Intn(len(m.feedbackChsForMateReqs))
	m.feedbackChsForMateReqs[index] <- input.UserIdWithMateRequest{
		TargetUserId:  targetUserId,
		SourceUserId:  sourceUserId,
		MateRequestId: requestId,
	}

	return nil
}

func (m *MateRequestUsecase) GetFbChansForGeoMessages() []<-chan input.UserIdWithMateRequest {
	chans := []<-chan input.UserIdWithMateRequest{}
	for i := range m.feedbackChsForMateReqs {
		chans = append(chans, m.feedbackChsForMateReqs[i]) // convert...
	}

	return chans
}
