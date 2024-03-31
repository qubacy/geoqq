package dto

import (
	dsDto "geoqq/internal/storage/domain/dto"
)

type PartProfileForUpdate struct {
	Description *string
	Privacy     *Privacy
	Security    *Security
}

type ProfileForUpdateInp struct {
	PartProfileForUpdate
	AvatarId *uint64
}

type ProfileWithAvatarForUpdateInp struct {
	PartProfileForUpdate
	Avatar *Avatar
}

// parts
// -----------------------------------------------------------------------

// Each field can be optional..?
type Privacy struct {
	HitMeUp *int
}

func (p *Privacy) ToDynamicDsInp() *dsDto.Privacy {
	return &dsDto.Privacy{
		HitMeUp: *p.HitMeUp,
	}
}

type Security struct {
	PasswordHash    string
	NewPasswordHash string
}

type Avatar struct {
	Ext     int
	Content string
}
