package database

type Database interface { // for any db
	UserDatabase
	MateDatabase
	GeoDatabase
}
