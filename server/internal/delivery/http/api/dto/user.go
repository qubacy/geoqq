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

func (self *Privacy) ToInp() *serviceDto.Privacy {
	return &serviceDto.Privacy{
		HitMeUp: &self.HitMeUp,
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

func (self *MyProfilePutReq) ToInp() serviceDto.UpdateProfileInp {
	var security *serviceDto.Security = nil
	var privacy *serviceDto.Privacy = nil
	var avatar *serviceDto.Avatar = nil

	if self.Security != nil {
		security = self.Security.ToInp()
	}
	if self.Privacy != nil {
		privacy = self.Privacy.ToInp()
	}
	if self.Avatar != nil {
		avatar = self.Avatar.ToInp()
	}

	// ***

	return serviceDto.UpdateProfileInp{
		Description: self.Description,
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

func (self *Security) ToInp() *serviceDto.Security {
	return &serviceDto.Security{
		Password:    self.Password,
		NewPassword: self.NewPassword,
	}
}

// -----------------------------------------------------------------------

type Avatar struct {
	Ext     float64 `json:"ext"`
	Content string  `json:"content"` // <--- base64-string
}

func (self *Avatar) ToInp() *serviceDto.Avatar {
	return &serviceDto.Avatar{
		Ext:     int(self.Ext), // or special type (pkg file)?
		Content: self.Content,
	}
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
