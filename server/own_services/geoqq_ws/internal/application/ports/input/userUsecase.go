package input

import (
	domain "common/pkg/domain/geoqq"
	"context"
	inputDto "geoqq_ws/internal/application/ports/input/dto"
)

type UserUsecase interface {
	UpdateUserLocation(ctx context.Context, data inputDto.UpdateUserLocation) error
	GetUserLocation(ctx context.Context, UserId uint64) (*domain.UserLocation, error)
}
