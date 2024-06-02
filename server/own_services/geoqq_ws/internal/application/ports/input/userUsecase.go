package wsApi

import (
	"context"
	inputDto "geoqq_ws/internal/application/ports/input/dto"
)

type UserUsecase interface {
	UpdateUserLocation(ctx context.Context,
		data inputDto.UpdateUserLocation) error
}
