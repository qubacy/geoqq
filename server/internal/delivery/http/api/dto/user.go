package dto

// GET /api/my-profile
// -----------------------------------------------------------------------

type MyProfileRes struct {
	Username    string `json:"username"`
	Description string `json:"description"`
	AvatarId    uint64 `json:"avatar-id"`
	Privacy     `json:"privacy"`
}

type Privacy struct {
	HitMeUp int `json:"hit-me-up"`
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

type MyProfilePutReq struct {
	AccessToken string  `json:"access-token"`
	Description *string `json:"description,omitempty"`
	Avatar      *string `json:"avatar,omitempty"` // <--- base64-string

	Privacy  *Privacy  `json:"privacy,omitempty"`
	Security *Security `json:"security,omitempty"`
}

type Security struct {
	Password    string `json:"password"`
	NewPassword string `json:"new-password"`
}
