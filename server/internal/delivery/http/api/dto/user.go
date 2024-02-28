package dto

import (
	"geoqq/internal/domain"
	serviceDto "geoqq/internal/service/dto"
)

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

func (s *Privacy) ToInp() *serviceDto.Privacy {
	return &serviceDto.Privacy{
		HitMeUp: &s.HitMeUp,
	}
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

type MyProfilePutReq struct {
	AccessToken string `json:"access-token"` // ?

	Description *string `json:"description,omitempty"`
	Avatar      *Avatar `json:"avatar,omitempty"`

	Privacy  *Privacy  `json:"privacy,omitempty"`
	Security *Security `json:"security,omitempty"`
}

func (s *MyProfilePutReq) ToInp() serviceDto.UpdateProfileInp {
	var security *serviceDto.Security = nil
	var privacy *serviceDto.Privacy = nil
	var avatar *serviceDto.Avatar = nil

	if s.Security != nil {
		security = s.Security.ToInp()
	}
	if s.Privacy != nil {
		privacy = s.Privacy.ToInp()
	}
	if s.Avatar != nil {
		avatar = s.Avatar.ToInp()
	}

	// ***

	return serviceDto.UpdateProfileInp{
		Description: s.Description,
		Security:    security,
		Privacy:     privacy,
		Avatar:      avatar,
	}
}

// -----------------------------------------------------------------------

type Security struct {
	Password    string `json:"password"`
	NewPassword string `json:"new-password"`
}

func (s *Security) ToInp() *serviceDto.Security {
	return &serviceDto.Security{
		Password:    s.Password,
		NewPassword: s.NewPassword,
	}
}

// -----------------------------------------------------------------------

type Avatar struct {
	Ext     float64 `json:"ext"`
	Content string  `json:"content"` // <--- base64-string
}

func (s *Avatar) ToInp() *serviceDto.Avatar {
	return &serviceDto.Avatar{
		Ext:     int(s.Ext), // or special type (pkg file)?
		Content: s.Content,
	}
}

// GET /api/user/{userId}
// -----------------------------------------------------------------------

type UserByIdRes struct {
	User
}

func MakeUserByIdResFromDomain(publicUser domain.PublicUser) UserByIdRes {
	return UserByIdRes{
		User: User{
			Username:    publicUser.Username,
			Description: publicUser.Description,
			AvatarId:    float64(publicUser.AvatarId),
			IsMate:      publicUser.IsMate,
		},
	}
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
