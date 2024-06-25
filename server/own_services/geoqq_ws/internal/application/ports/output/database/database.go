package database

import (
	"geoqq_ws/internal/application/ports"
	"geoqq_ws/internal/application/ports/output/database/background"
)

type Database interface { // for any db
	ports.Stoppable

	UserDatabase
	MateDatabase
	GeoDatabase

	background.UserDatabase
}
