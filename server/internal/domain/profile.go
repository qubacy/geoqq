package domain

type Profile struct {
	Username    string
	Description string
	AvatarId    uint64
	Privacy     Privacy
}

type Privacy struct {
	HitMeUp int
}
