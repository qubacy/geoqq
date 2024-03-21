package domain

// or Profile?
type UserProfile struct {
	Id          uint64
	Username    string
	Description string
	AvatarId    uint64
	Privacy     Privacy
}

type Privacy struct {
	HitMeUp int
}

func MakeUserProfile(
	id uint64, username, description string,
	avatarId uint64, hitMeUp int) UserProfile {

	return UserProfile{
		Id:          id,
		Username:    username,
		Description: description,
		AvatarId:    avatarId,
		Privacy: Privacy{
			HitMeUp: hitMeUp,
		},
	}
}
