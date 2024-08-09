package postgre

import (
	domain "common/pkg/domain/geoqq"
	"common/pkg/postgreUtils/wrappedPgxpool"
	utl "common/pkg/utility"
)

func ScanGeoMessage(scanner wrappedPgxpool.QueryResultScanner) (
	*domain.GeoMessage, error) {
	gm := domain.GeoMessage{}
	if err := scanner.Scan(&gm.Id, &gm.UserId, &gm.Text, &gm.Time); err != nil {
		return nil, utl.NewFuncError(ScanGeoMessage, err)
	}

	return &gm, nil
}
