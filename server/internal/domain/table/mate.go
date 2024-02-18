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
