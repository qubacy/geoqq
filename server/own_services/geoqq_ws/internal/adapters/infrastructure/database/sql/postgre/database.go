package postgre

import (
	"common/pkg/postgreUtils"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/adapters/infrastructure/database/sql/postgre/background"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Params postgreUtils.ConnectionParamsWithDb

type Database struct {
	*UserDatabase
	*MateDatabase
	*GeoDatabase

	*background.BgrDatabase
}

func New(startCtx context.Context, params Params) (*Database, error) {
	pool, err := pgxpool.Connect(startCtx,
		postgreUtils.CreateConnectionString(
			postgreUtils.ConnectionParamsWithDb(params)))

	var bgrDatabase *background.BgrDatabase
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error { return pool.Ping(startCtx) },
		func() error {
			bgrDatabase, err = background.New(pool)
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
