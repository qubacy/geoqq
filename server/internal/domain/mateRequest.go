package domain

type MateRequest struct {
	Id     uint64
	UserId uint64
}

type MateRequestList []MateRequest
