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
	*PublicUserStorage
	*UserProfileStorage
	*MateStorage
	*MateRequestStorage
	*MateChatStorage
	*MateChatMessageStorage
	*GeoChatMessageStorage

	*Background
}

type Dependencies struct {
	User          string
	Password      string
	Host          string
	Port          uint16
	DbName        string
	MaxQueryCount int
}

// ctor
// -----------------------------------------------------------------------

func NewStorage(ctxForInit, ctxWithCancel context.Context,
	deps Dependencies) (*Storage, error) {
	pool, err := pgxpool.Connect(ctxForInit, createConnectionString(deps))
	if err != nil {
		return nil, utility.NewFuncError(NewStorage, err)
	}

	storage := &Storage{
		AvatarStorage:          newAvatarStorage(pool),
		UserStorage:            newUserStorage(pool),
		PublicUserStorage:      newPublicUserStorage(pool),
		UserProfileStorage:     newUserProfileStorage(pool),
		MateStorage:            newMateStorage(pool),
		MateRequestStorage:     newMateRequestStorage(pool),
		MateChatStorage:        newMateChatStorage(pool),
		MateChatMessageStorage: newMateChatMessageStorage(pool),
		GeoChatMessageStorage:  newGeoChatMessageStorage(pool),

		Background: newBackgroundStorage(
			ctxForInit, ctxWithCancel,
			pool, deps.MaxQueryCount,
		),
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
