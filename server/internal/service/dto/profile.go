package dto

import (
	dsDto "geoqq/internal/storage/domain/dto"
)

type UpdateProfileInp struct {
	Description *string
	Avatar      *Avatar

	Privacy  *Privacy
	Security *Security
}

// every field is optional...
type Privacy struct {
	HitMeUp *int
}

func (p *Privacy) ToDynamicDsInp() *dsDto.Privacy {
	return &dsDto.Privacy{
		HitMeUp: *p.HitMeUp,
	}
}

type Security struct {
	Password    string
	NewPassword string
}

type Avatar struct {
	Ext     int
	Content string
}
