package domain

// or User?
type PublicUser struct {
	Username    string
	Description string
	AvatarId    uint64
	IsMate      bool
}

type PublicUserList []*PublicUser
