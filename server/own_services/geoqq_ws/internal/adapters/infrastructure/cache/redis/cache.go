package redis

import (
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_ws/internal/application/ports/output/cache"

	"github.com/redis/go-redis/v9"
)

type Params struct {
	Host     string
	Port     uint16
	User     string
	Password string
	DbIndex  int
}

// -----------------------------------------------------------------------

type Cache struct {
	client *redis.Client
}

func New(startCtx context.Context, params *Params) (*Cache, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     fmt.Sprintf("%v:%v", params.Host, params.Port),
		Username: params.User,
		Password: params.Password,
		DB:       params.DbIndex,
	})

	statusCmd := client.Ping(startCtx)
	if err := statusCmd.Err(); err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	return &Cache{
		client: client,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (h *Cache) AddUserLocation(userId uint64, loc cache.Location) error {
	return nil
}

func (h *Cache) GetUserLocation(userId uint64) (bool, cache.Location, error) {
	return false, cache.Location{}, nil
}

func (h *Cache) SearchUsersNearby(loc cache.Location, radius uint64) ([]uint64, error) {
	return nil, nil
}

func (h *Cache) RemoveAllForUser(userId uint64) error {
	return nil
}
