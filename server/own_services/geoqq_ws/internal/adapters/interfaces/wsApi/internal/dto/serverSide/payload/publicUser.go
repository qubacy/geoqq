package payload

import (
	domain "common/pkg/domain/geoqq"
)

type PublicUser struct {
	Id             float64 `json:"id"`
	Username       string  `json:"username"`
	Description    string  `json:"description"`
	AvatarId       float64 `json:"avatar-id"`
	LastActionTime float64 `json:"last-action-time"`
	IsMate         bool    `json:"is-mate"`
	IsDeleted      bool    `json:"is-deleted"`
	HitMeUp        float64 `json:"hit-me-up"`
}

func PublicUserFromDomain(dm *domain.PublicUser) (*PublicUser, error) {
	return &PublicUser{
		Id:             float64(dm.Id),
		Username:       dm.Username,
		Description:    dm.Description,
		AvatarId:       float64(dm.AvatarId),
		LastActionTime: float64(dm.LastActionTime.Unix()),
		IsMate:         dm.IsMate,
		IsDeleted:      dm.IsDeleted,
		HitMeUp:        float64(dm.HitMeUp),
	}, nil
}
