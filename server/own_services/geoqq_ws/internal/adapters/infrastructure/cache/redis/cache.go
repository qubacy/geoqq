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

const (
	keyUsers = "users"
)

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

func (h *Cache) AddUserLocation(ctx context.Context, userId uint64, loc cache.Location) error {
	err := h.client.GeoAdd(ctx, keyUsers, &redis.GeoLocation{
		Name:      fmt.Sprintf("%v", userId),
		Longitude: loc.Lon,
		Latitude:  loc.Lat,
	}).Err()

	if err != nil {
		return utl.NewFuncError(h.AddUserLocation, err)
	}

	return nil // ok
}

func (h *Cache) GetUserLocation(ctx context.Context, userId uint64) (bool, cache.Location, error) {
	return false, cache.Location{}, nil
}

func (h *Cache) SearchUsersNearby(ctx context.Context, loc cache.Location, radius uint64) ([]uint64, error) {
	return nil, nil
}

func (h *Cache) RemoveAllForUser(ctx context.Context, userId uint64) error {
	return nil
}
