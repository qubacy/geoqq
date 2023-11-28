package domain

import "time"

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
	Time      time.Time
}

type UserDetails struct {
	UserId      uint64
	Description string
	Avatar      string
}

type UserOptions struct {
	UserId  uint64
	Privacy int
}
