package postgre

import "github.com/jackc/pgx/v4/pgxpool"

type GeoDatabase struct {
	pool *pgxpool.Pool
}

func newGeoDatabase(pool *pgxpool.Pool) *GeoDatabase {
	return &GeoDatabase{
		pool: pool,
	}
}
