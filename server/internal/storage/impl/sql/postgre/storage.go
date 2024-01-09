package postgre

import (
	"context"
	"fmt"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type Storage struct {
	userStorage *UserStorage
}

type Dependencies struct {
	User     string
	Password string
	Host     string
	Port     uint16
	DbName   string
}

// ctor
// -----------------------------------------------------------------------

func NewStorage(ctx context.Context, deps Dependencies) (*Storage, error) {
	pool, err := pgxpool.Connect(ctx, createConnectionString(deps))
	if err != nil {
		return nil, utility.CreateCustomError(NewStorage, err)
	}

	return &Storage{
		userStorage: newUserStorage(pool),
	}, nil
}

func createConnectionString(deps Dependencies) string {
	return fmt.Sprintf(
		"user=%v password=%v "+
			"host=%v port=%v database=%v",
		deps.User, deps.Password,
		deps.Host, deps.Port, deps.DbName,
	)
}
