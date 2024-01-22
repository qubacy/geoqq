package impl

import (
	domainStorage "geoqq/internal/storage/domain"
)

type UserService struct {
	storage domainStorage.Storage
}
