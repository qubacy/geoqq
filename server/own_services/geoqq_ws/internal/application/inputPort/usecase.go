package inputPort

import (
	"context"
	"geoqq_ws/internal/application/inputPort/dto"
)

type UserUsecase interface {
	AddUserLocation(ctx context.Context, data dto.AddUserLocation) error
}
