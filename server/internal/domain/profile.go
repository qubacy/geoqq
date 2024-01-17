package domain

type Profile struct {
	Username    string
	Description string
	AvatarId    uint64
	Privacy     Privacy
}

func MakeProfile(username, description string,
	avatarId uint64, hitMeUp int) Profile {

	return Profile{
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
