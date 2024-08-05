package input

import (
	"context"
)

type PublicUserUsecase interface {
	InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error
}
