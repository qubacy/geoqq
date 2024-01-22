package postgre

import (
	"context"
	"fmt"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

// consists of concrete implementation types!
type Storage struct {
	*AvatarStorage
	*UserStorage
	*UserProfileStorage
	*MateRequestStorage
	*MateChatStorage
	*GeoChatStorage
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
		return nil, utility.NewFuncError(NewStorage, err)
	}

	storage := &Storage{
		UserStorage:        newUserStorage(pool),
		UserProfileStorage: newUserProfileStorage(pool),
	}

	return storage, nil
}

func createConnectionString(deps Dependencies) string {
	return fmt.Sprintf(
		"user=%v password=%v "+
			"host=%v port=%v database=%v",
		deps.User, deps.Password,
		deps.Host, deps.Port, deps.DbName,
	)
}
