package dto

import "geoqq/internal/domain"

// GET /api/my-profile
// -----------------------------------------------------------------------

type MyProfileRes struct {
	Profile
}

func MakeMyProfileRes(value domain.UserProfile) MyProfileRes {
	return MyProfileRes{
		Profile: Profile{
			Username:    value.Username,
			Description: value.Description,
			AvatarId:    float64(value.AvatarId), // or null/optional?
			Privacy:     MakePrivacy(value.Privacy),
		},
	}
}

type Profile struct { // not equal struct user!
	Username    string  `json:"username"`
	Description string  `json:"description"`
	AvatarId    float64 `json:"avatar-id"`

	Privacy `json:"privacy"`
}

type Privacy struct {
	HitMeUp int `json:"hit-me-up"`
}

func MakePrivacy(privacy domain.Privacy) Privacy {
	return Privacy{
		HitMeUp: privacy.HitMeUp,
	}
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

type MyProfilePutReq struct {
	AccessToken string  `json:"access-token"` // ?
	Description *string `json:"description,omitempty"`
	Avatar      *string `json:"avatar,omitempty"` // <--- base64-string

	Privacy  *Privacy  `json:"privacy,omitempty"`
	Security *Security `json:"security,omitempty"`
}

type Security struct {
	Password    string `json:"password"`
	NewPassword string `json:"new-password"`
}

// GET /api/user/{userId}
// -----------------------------------------------------------------------

type UserByIdRes struct {
	User
}

type User struct {
	Username    string  `json:"username"`
	Description string  `json:"description"`
	AvatarId    float64 `json:"avatar-id"`
	IsMate      bool    `json:"is-mate"`
}

// GET /api/user
// -----------------------------------------------------------------------

type SomeUsersReq struct {
	Ids []float64 `json:"ids"`
}

type SomeUsersRes struct {
	Users []User `json:"users"`
}
