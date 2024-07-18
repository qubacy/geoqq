package input

import (
	"context"
	dd "geoqq_ws/internal/application/domain"
	inputDto "geoqq_ws/internal/application/ports/input/dto"
)

type UserUsecase interface {
	UpdateUserLocation(ctx context.Context, data inputDto.UpdateUserLocation) error
	GetUserLocation(ctx context.Context, UserId uint64) (*dd.UserLocation, error)
}
