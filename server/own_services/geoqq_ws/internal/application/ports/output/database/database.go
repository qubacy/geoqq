package database

type Database interface {
	UserDatabase
	MateDatabase
	GeoDatabase
}
