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

func NewMyProfileRes(value *domain.UserProfile) (*MyProfileRes, error) {
	if value == nil {
		return nil, ErrNilInputParameterWithName("UserProfile")
	}

	return &MyProfileRes{
		Profile: Profile{
			Id:          float64(value.Id),
			Login:       value.Login,
			Username:    value.Username,
			Description: value.Description,
			AvatarId:    float64(value.AvatarId), // or null/optional?
			Privacy:     MakePrivacy(value.Privacy),
		},
	}, nil
}

type Profile struct { // not equal struct user!
	Id          float64 `json:"id"`
	Login       string  `json:"login"`
	Username    string  `json:"username"`
	Description string  `json:"description"`
	AvatarId    float64 `json:"avatar-id"`

	Privacy `json:"privacy"`
}

type Privacy struct {
	HitMeUp float64 `json:"hit-me-up"` // without binding since zero is ignored!
}

func MakePrivacy(privacy domain.Privacy) Privacy {
	return Privacy{
		HitMeUp: float64(privacy.HitMeUp),
	}
}

func (s *Privacy) ToDynamicInp() *serviceDto.Privacy {
	hitMeUp := new(int)
	*hitMeUp = int(s.HitMeUp)

	return &serviceDto.Privacy{
		HitMeUp: hitMeUp,
	}
}

// PUT /api/my-profile
// -----------------------------------------------------------------------

type PartMyProfileForPutReq struct {
	Username    *string `json:"username,omitempty"`
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
		Username:    s.Username,
		Description: s.Description,

		Security: security,
		Privacy:  privacy,
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
	Password    string `json:"password" binding:"required"` // password hash in base64
	NewPassword string `json:"new-password" binding:"required"`
}

func (s *Security) ToDynamicInp() *serviceDto.Security {
	return &serviceDto.Security{
		PasswordHash:    s.Password,
		NewPasswordHash: s.NewPassword,
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
		return UserByIdRes{}, ErrNilInputParameter
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
	Id             float64 `json:"id"`
	Username       string  `json:"username"` // public!
	Description    string  `json:"description"`
	AvatarId       float64 `json:"avatar-id"`
	LastActionTime float64 `json:"last-action-time"`
	IsMate         bool    `json:"is-mate"`
	IsDeleted      bool    `json:"is-deleted"`
	HitMeUp        float64 `json:"hit-me-up"`
}

func MakeUserFromDomain(publicUser *domain.PublicUser) (User, error) {
	if publicUser == nil {
		return User{}, ErrNilInputParameter
	}

	return User{
		Id:             float64(publicUser.Id),
		Username:       publicUser.Username,
		Description:    publicUser.Description,
		AvatarId:       float64(publicUser.AvatarId),
		LastActionTime: float64(publicUser.LastActionTime.Unix()),
		IsMate:         publicUser.IsMate,
		IsDeleted:      publicUser.IsDeleted,
		HitMeUp:        float64(publicUser.HitMeUp),
	}, nil
}

// GET /api/user
// -----------------------------------------------------------------------

type SomeUsersReq struct {
	Ids []float64 `json:"ids" binding:"required"`
}

type SomeUsersRes struct {
	Users []User `json:"users"`
}

func NewSomeUsersResFromDomain(publicUsers []*domain.PublicUser) (
	*SomeUsersRes, error,
) {
	if publicUsers == nil {
		return nil, ErrNilInputParameter
	}

	users := make([]User, 0, len(publicUsers))
	for i := range publicUsers {
		user, err := MakeUserFromDomain(publicUsers[i])
		if err != nil {
			return nil, utility.NewFuncError(
				NewSomeUsersResFromDomain, err)
		}

		users = append(users, user)
	}

	return &SomeUsersRes{
		Users: users,
	}, nil
}
