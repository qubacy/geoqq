package impl

import (
	domainStorage "geoqq/internal/storage/domain"
)

type ImageService struct {
	storage domainStorage.Storage
}
