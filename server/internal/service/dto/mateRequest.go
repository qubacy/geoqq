package dto

import "geoqq/internal/domain/table"

type MateRequestsForUserOut struct {
	TargetUserId uint64
	MateRequests []*MateRequest
}

func NewMateRequestsForUserOutFromDomain(
	mateRequests []*table.MateRequest, userId uint64) *MateRequestsForUserOut {

	// TODO: check arg on nil?

	outputMateRequests := []*MateRequest{}
	for i := range mateRequests {
		outputMateRequest := NewMateRequest(
			mateRequests[i].Id,
			mateRequests[i].FromUserId,
		)
		outputMateRequests = append(
			outputMateRequests,
			outputMateRequest,
		)
	}

	return &MateRequestsForUserOut{
		MateRequests: outputMateRequests,
		TargetUserId: userId,
	}
}

// parts
// -----------------------------------------------------------------------

type MateRequest struct {
	Id           uint64
	SourceUserId uint64
}

func NewMateRequest(id uint64, sourceUserId uint64) *MateRequest {
	return &MateRequest{
		Id:           id,
		SourceUserId: sourceUserId,
	}
}
