package domain

import "time"

// or User?
type PublicUser struct {
	Id             uint64
	Username       string
	Description    string
	AvatarId       uint64
	LastActionTime time.Time
	IsMate         bool
	IsDeleted      bool
	HitMeUp        int
}

type PublicUserList []*PublicUser
