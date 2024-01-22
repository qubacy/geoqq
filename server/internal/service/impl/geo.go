package impl

import (
	domainStorage "geoqq/internal/storage/domain"
)

type GeoService struct {
	storage domainStorage.Storage
}
