package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
)

type UserIdWithPublicUser struct {
	UserIdWithEvent
	PublicUser *domain.PublicUser // with info for target user!
}

// ----------------------------------------------------------------------

type PublicUserUsecase interface {
	InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error
	GetFbChansForPublicUser() []<-chan UserIdWithPublicUser
}
