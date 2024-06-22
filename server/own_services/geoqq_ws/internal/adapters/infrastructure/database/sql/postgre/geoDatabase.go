package postgre

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"

	"github.com/jackc/pgx/v4/pgxpool"
)

type GeoDatabase struct {
	pool *pgxpool.Pool
}

func newGeoDatabase(pool *pgxpool.Pool) *GeoDatabase {
	return &GeoDatabase{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (g *GeoDatabase) InsertGeoMessage(ctx context.Context, fromUserId uint64,
	text string, lon, lat float64) (uint64, error) {
	sourceFunc := g.InsertGeoMessage
	c, err := g.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}
	defer c.Release() // !

	row := c.QueryRow(ctx,
		template.InsertGeoChatMessage+`;`,
		fromUserId, text, lat, lon)

	if gmId, err := wrappedPgxpool.ScanUint64(row, sourceFunc); err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	} else {
		return gmId, nil
	}
}

func (g *GeoDatabase) GetGeoMessageWithId(ctx context.Context, id uint64) (
	*domain.GeoMessage, error) {
	sourceFunc := g.GetGeoMessageWithId
	var err error

	row, err := queryRow(ctx, g.pool,
		template.SelectGeoChatMessage, id)

	var gm *domain.GeoMessage
	utl.RunFuncsRetErr(
		func() error { return err },
		func() error {
			gm, err = scanGeoMessage(row)
			return err
		})
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return gm, nil
}

// scan
// -----------------------------------------------------------------------

func scanGeoMessage(scanner wrappedPgxpool.QueryResultScanner) (
	*domain.GeoMessage, error) {
	gm := domain.GeoMessage{}
	if err := scanner.Scan(&gm.Id, &gm.UserId, &gm.Text, &gm.Time); err != nil {
		return nil, utl.NewFuncError(scanGeoMessage, err)
	}

	return &gm, nil
}
