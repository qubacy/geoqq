package postgre

import (
	"common/pkg/postgreUtils"
	"common/pkg/utility"
	"context"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Dependencies postgreUtils.ConnectionParamsWithDb

type Database struct {
	*UserDatabase
	*MateDatabase
	*GeoDatabase
}

func New(ctxForInit context.Context, deps Dependencies) (*Database, error) {
	pool, err := pgxpool.Connect(ctxForInit,
		postgreUtils.CreateConnectionString(
			postgreUtils.ConnectionParamsWithDb(deps)))

	if err != nil {
		return nil, utility.NewFuncError(New, err)
	}

	return &Database{
		UserDatabase: newUserDatabase(pool),
		MateDatabase: newMateDatabase(pool),
		GeoDatabase:  newGeoDatabase(pool),
	}, nil
}
