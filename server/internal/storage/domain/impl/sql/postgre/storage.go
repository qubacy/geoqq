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
	*MateStorage
	*MateRequestStorage
	*MateChatStorage
	*MateChatMessageStorage
	*GeoChatMessageStorage
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
		AvatarStorage:          newAvatarStorage(pool),
		UserStorage:            newUserStorage(pool),
		UserProfileStorage:     newUserProfileStorage(pool),
		MateStorage:            newMateStorage(pool),
		MateRequestStorage:     newMateRequestStorage(pool),
		MateChatStorage:        newMateChatStorage(pool),
		MateChatMessageStorage: newMateChatMessageStorage(pool),
		GeoChatMessageStorage:  newGeoChatMessageStorage(pool),
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
