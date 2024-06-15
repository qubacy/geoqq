package usecase

import (
	// Output Ports
	"geoqq_ws/internal/application/ports/output/database"
)

type Dependencies struct {
	Database  database.Database
	Messaging messaging.Messaging
}

type Usecase struct {
	*UserUsecase
}

func New(deps Dependencies) (*Usecase, error) {
	return &Usecase{
		UserUsecase: newUserUsecase(deps),
	}, nil
}
