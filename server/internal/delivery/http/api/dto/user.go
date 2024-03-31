package dto

import (
	"geoqq/internal/domain"
	serviceDto "geoqq/internal/service/dto"
	"geoqq/pkg/utility"
)

// GET /api/my-profile
// -----------------------------------------------------------------------

type MyProfileRes struct {
	Profile
}

func MakeMyProfileRes(value domain.UserProfile) MyProfileRes {
	return MyProfileRes{
		Profile: Profile{
			Id:          float64(value.Id),
			Username:    value.Username,
			Description: value.Description,
			AvatarId:    float64(value.AvatarId), // or null/optional?
			Privacy:     MakePrivacy(value.Privacy),
		},
	}
}

type Profile struct { // not equal struct user!
	Id          float64 `json:"id"`
	Username    string  `json:"username"`
	Description string  `json:"description"`
	AvatarId    float64 `json:"avatar-id"`

	Privacy `json:"privacy"`
}

type Privacy struct {
	HitMeUp int `json:"hit-me-up"` // without binding since zero is ignored!
}

func MakePrivacy(privacy domain.Privacy) Privacy {
	return Privacy{
		HitMeUp: privacy.HitMeUp,
	}
}

func (s *Privacy) ToDynamicInp() *serviceDto.Privacy {
	return &serviceDto.Privacy{
		HitMeUp: &s.HitMeUp,
	}
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

type PartMyProfileForPutReq struct {
	AccessToken string `json:"access-token" binding:"required"` // ?

	Description *string `json:"description,omitempty"`

	Privacy  *Privacy  `json:"privacy,omitempty"`
	Security *Security `json:"security,omitempty"`
}

func (s *PartMyProfileForPutReq) ToPartProfileForUpdate() serviceDto.PartProfileForUpdate {
	var security *serviceDto.Security = nil
	var privacy *serviceDto.Privacy = nil

	if s.Security != nil {
		security = s.Security.ToDynamicInp()
	}
	if s.Privacy != nil {
		privacy = s.Privacy.ToDynamicInp()
	}

	return serviceDto.PartProfileForUpdate{
		Description: s.Description,
		Security:    security,
		Privacy:     privacy,
	}
}

func (s *PartMyProfileForPutReq) ToPartProfileForUpdateInp() serviceDto.ProfileForUpdateInp {
	return serviceDto.ProfileForUpdateInp{
		PartProfileForUpdate: s.ToPartProfileForUpdate(),
		AvatarId:             nil,
	}
}

func (s *PartMyProfileForPutReq) ToPartProfileForUpdateWithAvatarInp() serviceDto.ProfileWithAvatarForUpdateInp {
	return serviceDto.ProfileWithAvatarForUpdateInp{
		PartProfileForUpdate: s.ToPartProfileForUpdate(),
		Avatar:               nil,
	}
}

// -----------------------------------------------------------------------

type MyProfilePutReq struct {
	PartMyProfileForPutReq
	AvatarId *float64 `json:"avatar-id,omitempty"`
}

func (s *MyProfilePutReq) ToInp() serviceDto.ProfileForUpdateInp {
	var avatarId *uint64 = nil
	if s.AvatarId != nil {
		avatarId = new(uint64)
		*avatarId = uint64(*s.AvatarId)
	}

	// ***

	input := s.ToPartProfileForUpdateInp()
	input.AvatarId = avatarId
	return input
}

// PUT /api/my-profile/with-attached-avatar
// -----------------------------------------------------------------------

type MyProfileWithAttachedAvatarPutReq struct {
	PartMyProfileForPutReq
	Avatar *Avatar `json:"avatar,omitempty"`
}

func (s *MyProfileWithAttachedAvatarPutReq) ToInp() serviceDto.ProfileWithAvatarForUpdateInp {
	var avatar *serviceDto.Avatar = nil
	if s.Avatar != nil {
		avatar = s.Avatar.ToDynamicInp()
	}

	// ***

	input := s.ToPartProfileForUpdateWithAvatarInp()
	input.Avatar = avatar
	return input
}

// -----------------------------------------------------------------------

type Security struct {
	Password    string `json:"password" binding:"required"`
	NewPassword string `json:"new-password" binding:"required"`
}

func (s *Security) ToDynamicInp() *serviceDto.Security {
	return &serviceDto.Security{
		Password:    s.Password,
		NewPassword: s.NewPassword,
	}
}

// -----------------------------------------------------------------------

// type Avatar ImageWithoutId

type Avatar struct {
	Ext     float64 `json:"ext" binding:"required"`
	Content string  `json:"content" binding:"required"` // <--- base64-string
}

func (s *Avatar) ToDynamicInp() *serviceDto.Avatar {
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

func MakeUserByIdResFromDomain(publicUser *domain.PublicUser) (UserByIdRes, error,
) {
	if publicUser == nil {
		return UserByIdRes{}, ErrInputParameterIsNil
	}

	user, err := MakeUserFromDomain(publicUser)
	if err != nil {
		return UserByIdRes{}, utility.NewFuncError(
			MakeUserByIdResFromDomain, err)
	}

	return UserByIdRes{
		User: user,
	}, nil
}

// -----------------------------------------------------------------------

type User struct {
	Id          float64 `json:"id"`
	IsDeleted   bool    `json:"is-deleted"`
	Username    string  `json:"username"`
	Description string  `json:"description"`
	AvatarId    float64 `json:"avatar-id"`
	IsMate      bool    `json:"is-mate"`
}

func MakeUserFromDomain(publicUser *domain.PublicUser) (User, error) {
	if publicUser == nil {
		return User{}, ErrInputParameterIsNil
	}

	return User{
		Id:          float64(publicUser.Id),
		IsDeleted:   publicUser.IsDeleted,
		Username:    publicUser.Username,
		Description: publicUser.Description,
		AvatarId:    float64(publicUser.AvatarId),
		IsMate:      publicUser.IsMate,
	}, nil
}

// GET /api/user
// -----------------------------------------------------------------------

type SomeUsersReq struct {
	AccessToken string    `json:"access-token" binding:"required"` // ?
	Ids         []float64 `json:"ids" binding:"required"`
}

type SomeUsersRes struct {
	Users []User `json:"users"`
}

func MakeSomeUsersResFromDomain(publicUsers []*domain.PublicUser) (
	SomeUsersRes, error,
) {
	if publicUsers == nil {
		return SomeUsersRes{}, ErrInputParameterIsNil
	}

	users := make([]User, 0, len(publicUsers))
	for i := range publicUsers {
		user, err := MakeUserFromDomain(publicUsers[i])
		if err != nil {
			return SomeUsersRes{}, utility.NewFuncError(
				MakeSomeUsersResFromDomain, err)
		}

		users = append(users, user)
	}

	return SomeUsersRes{
		Users: users,
	}, nil
}
