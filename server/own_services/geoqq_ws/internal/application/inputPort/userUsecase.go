package inputPort

import (
	"context"
	"geoqq_ws/internal/application/inputPort/dto"
)

type UserUsecase interface {
	UpdateUserLocation(ctx context.Context, data dto.UpdateUserLocation) error
}
