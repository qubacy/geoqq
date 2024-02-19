package table

import "time"

type MateRequestResult int16

const (
	Waiting MateRequestResult = iota
	Accepted
	Rejected
)

func MakeMateResultFromBool(value bool) MateRequestResult {
	if value {
		return Accepted
	}
	return Rejected
}

func MakeMateResultFromInt(value int16) (MateRequestResult, error) {
	switch value {
	case int16(Waiting):
		return Waiting, nil

	case int16(Accepted):
		return Accepted, nil

	case int16(Rejected):
		return Rejected, nil
	}

	return Waiting, ErrUnknownMateResult
}

func (mrs MateRequestResult) IsAcceptedOrRejected() bool {
	if mrs == Accepted || mrs == Rejected {
		return true
	}
	return false
}

func (mrs MateRequestResult) IsAccepted() bool {
	return mrs == Accepted
}

type Mate struct {
	Id           uint64
	FirstUserId  uint64
	SecondUserId uint64
}

type MateRequest struct {
	Id         uint64
	FromUserId uint64
	ToUserId   uint64

	RequestTime  time.Time
	ResponseTime time.Time

	Result MateRequestResult
}
