package payload

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
