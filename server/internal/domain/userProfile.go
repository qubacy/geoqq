package domain

// or Profile?
type UserProfile struct {
	Username    string
	Description string
	AvatarId    uint64
	Privacy     Privacy
}

func MakeUserProfile(username, description string,
	avatarId uint64, hitMeUp int) UserProfile {

	return UserProfile{
		Username:    username,
		Description: description,
		AvatarId:    avatarId,
		Privacy: Privacy{
			HitMeUp: hitMeUp,
		},
	}
}

type Privacy struct {
	HitMeUp int
}
