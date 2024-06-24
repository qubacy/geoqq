package database

import (
	"geoqq_ws/internal/application/ports/output/database/background"
)

type Database interface { // for any db
	UserDatabase
	MateDatabase
	GeoDatabase

	background.UserDatabase
}
