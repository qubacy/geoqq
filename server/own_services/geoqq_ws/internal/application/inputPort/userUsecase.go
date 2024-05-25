package wsApi

import (
	"context"
	"geoqq_ws/internal/application/inputPort/wsApi/dto"
)

type UserUsecase interface {
	UpdateUserLocation(ctx context.Context,
		data dto.UpdateUserLocation) error
}
