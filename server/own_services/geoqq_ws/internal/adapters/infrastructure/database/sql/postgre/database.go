package postgre

import (
	"common/pkg/postgreUtils"
	utl "common/pkg/utility"
	"context"
	"errors"
	bgr "geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/background"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Params postgreUtils.ConnectionParamsWithDb
type BgrParams bgr.Params

type Database struct {
	pool *pgxpool.Pool

	*UserDatabase
	*MateDatabase
	*GeoDatabase

	*bgr.BgrDatabase
}

func New(startCtx context.Context,
	params Params, bgrParams bgr.Params) (*Database, error) {

	pool, err := pgxpool.Connect(startCtx,
		postgreUtils.CreateConnectionString(
			postgreUtils.ConnectionParamsWithDb(params)))

	var bgrDatabase *bgr.BgrDatabase
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error { return pool.Ping(startCtx) },
		func() error {
			bgrDatabase, err = bgr.New(startCtx, pool, bgrParams)
			return err
		})
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	return &Database{
		UserDatabase: newUserDatabase(pool),
		MateDatabase: newMateDatabase(pool),
		GeoDatabase:  newGeoDatabase(pool),

		BgrDatabase: bgrDatabase,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (d *Database) Stop(stopCtx context.Context) error {
	err := errors.Join(
		d.BgrDatabase.Stop(stopCtx),
	)

	d.pool.Close() // only used here...
	return err
}
