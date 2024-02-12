package dto

type UpdateUserPartsInp struct {
	Description  *string
	Privacy      *Privacy
	HashPassword *string
	AvatarId     *uint64 // content to file storage!
}

type Privacy struct {
	HitMeUp int
}
