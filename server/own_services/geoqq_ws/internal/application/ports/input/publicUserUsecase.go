package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type UserIdWithPublicUser struct {
	UserId     uint64
	PublicUser *domain.PublicUser // with info for target user!
}

func (u UserIdWithPublicUser) GetUserId() uint64 {
	return u.UserId
}

// ----------------------------------------------------------------------

type PublicUserUsecase interface {
	InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error
	GetFbChansForPublicUser() []<-chan UserIdWithPublicUser
}
