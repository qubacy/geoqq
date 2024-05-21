package usecase

import (
	// Output Ports
	"geoqq_ws/internal/application/outputPort/database"
	"geoqq_ws/internal/application/outputPort/messaging"
)

type Dependencies struct {
	Db        database.Database
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
