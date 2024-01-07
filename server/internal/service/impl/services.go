package impl

import "geoqq/internal/storage"

type Services struct {
	storage storage.Storage
}

type Dependencies struct {
	Storage storage.Storage
}

func NewServices(deps Dependencies) (*Services, error) {
	return &Services{
		storage: deps.Storage,
	}, nil
}
