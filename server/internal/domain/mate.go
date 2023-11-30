package domain

import "time"

type Mate struct {
	Id           uint64
	FirstUserId  uint64
	SecondUserId uint64
}

type MateRequest struct {
	Id           uint64
	FromUserId   uint64
	ToUserId     uint64
	RequestTime  time.Time
	ResponseTime time.Time
	Result       string
}
