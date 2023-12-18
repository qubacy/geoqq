package domain

import "time"

const (
	HitMeUpYes = 0
	HitMeUpNo  = 1
)

// -----------------------------------------------------------------------

type User struct {
	UserEntry
	UserLocation
	UserDetails
	UserOptions
}

type Users []User

type UserEntry struct {
	Id           uint64 // <--- bigserial
	Username     string
	HashPassword string
	HashUpdToken string
	SignUpTime   time.Time
	SignInTime   time.Time
}

type UserLocation struct {
	UserId    uint64
	Longitude float64
	Latitude  float64
	Time      *time.Time
}

type UserDetails struct {
	UserId      uint64
	Description string
	AvatarId    *uint64
}

type UserOptions struct {
	UserId  uint64
	HitMeUp int
}
