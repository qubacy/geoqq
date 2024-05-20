package dto

import (
	"geoqq_http/internal/domain/table"
)

type UpdateUserPartsInp struct {
	Username           *string
	Description        *string
	Privacy            *Privacy
	PasswordDoubleHash *string
	AvatarId           *uint64 // content to file storage!
}

type Privacy struct {
	HitMeUp int
}

func MakePrivacyForDeletedUser() Privacy {
	return Privacy{
		HitMeUp: table.HitMeUpNo,
		//...
	}
}
