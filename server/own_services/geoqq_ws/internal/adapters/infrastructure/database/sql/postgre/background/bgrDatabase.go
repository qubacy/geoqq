package background // internal

import (
	"github.com/jackc/pgx/v4/pgxpool"
)

type BgrDatabase struct {
	*UserDatabase
}

func New(pool *pgxpool.Pool) (*BgrDatabase, error) {
	return &BgrDatabase{
		UserDatabase: newUserDatabase(pool),
	}, nil
}
