package input

import (
	"context"
)

type PublicUserUsecase interface {
	InformAboutPublicUserUpdate(ctx context.Context, userId uint64) error
}
