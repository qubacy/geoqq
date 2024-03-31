package dto

type UpdateUserPartsInp struct {
	Description        *string
	Privacy            *Privacy
	PasswordDoubleHash *string
	AvatarId           *uint64 // content to file storage!
}

type Privacy struct {
	HitMeUp int
}
