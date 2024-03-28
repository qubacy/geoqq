package domain

// or User?
type PublicUser struct {
	Id          uint64
	IsDeleted   bool
	Username    string
	Description string
	AvatarId    uint64
	IsMate      bool
}

type PublicUserList []*PublicUser
