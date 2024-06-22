package redis

import (
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_ws/internal/application/ports/output/cache"
	"strconv"

	"github.com/redis/go-redis/v9"
)

const (
	keyUserLocations = "user_locations"
)

const (
	radiusUnit = "m"
)

func makeCacheKeyForUserId(userId uint64) string {
	return fmt.Sprintf("%d", userId)
}

func cacheKeyToUserId(value string) (uint64, error) {
	userId, err := strconv.ParseUint(value, 10, 64)
	if err != nil {
		return 0, utl.NewFuncError(cacheKeyToUserId, err)
	}

	return userId, nil
}

// -----------------------------------------------------------------------

type Params struct {
	Host     string
	Port     uint16
	User     string
	Password string
	DbIndex  int
}

type Cache struct {
	rdb *redis.Client
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
		rdb: client,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (h *Cache) AddUserRadius(ctx context.Context, userId uint64, radius uint64) error {
	cacheKey := makeCacheKeyForUserId(userId)
	if err := h.rdb.Set(ctx, cacheKey, radius, 0).Err(); err != nil {
		return utl.NewFuncError(h.AddUserRadius, err)
	}

	return nil
}

func (h *Cache) GetUserRadius(ctx context.Context, userId uint64) (uint64, error) {
	cacheKey := makeCacheKeyForUserId(userId)

	var radiusStr string
	var radius uint64
	var err error

	err = utl.RunFuncsRetErr(
		func() error {
			radiusStr, err = h.rdb.Get(ctx, cacheKey).Result()
			return err
		}, func() error {
			radius, err = strconv.ParseUint(radiusStr, 10, 64)
			return err
		})
	if err != nil {
		return 0, utl.NewFuncError(h.GetUserRadius, err)
	}

	return radius, nil // ok
}

// -----------------------------------------------------------------------

func (h *Cache) AddUserLocation(ctx context.Context, userId uint64, loc cache.Location) error {
	cacheKey := makeCacheKeyForUserId(userId)
	err := h.rdb.GeoAdd(ctx, keyUserLocations, &redis.GeoLocation{
		Name:      cacheKey,
		Longitude: loc.Lon,
		Latitude:  loc.Lat,
	}).Err()

	if err != nil {
		return utl.NewFuncError(h.AddUserLocation, err)
	}

	return nil // ok
}

func (h *Cache) GetUserLocation(ctx context.Context, userId uint64) (bool, cache.Location, error) {
	cacheKey := makeCacheKeyForUserId(userId)
	coord, err := h.rdb.GeoPos(ctx, keyUserLocations, cacheKey).Result()
	if err != nil {
		return false, cache.Location{},
			utl.NewFuncError(h.AddUserLocation, err)
	}
	if coord[0] == nil {
		return false, cache.Location{}, nil
	}

	loc := cache.Location{
		Lon: coord[0].Longitude,
		Lat: coord[0].Latitude,
	}
	return true, loc, nil
}

func (h *Cache) SearchUsersNearby(ctx context.Context, loc cache.Location, radius uint64) ([]uint64, error) {
	sourceFunc := h.SearchUsersNearby

	members, err := h.rdb.GeoSearch(ctx, keyUserLocations, &redis.GeoSearchQuery{
		Longitude:  loc.Lon,
		Latitude:   loc.Lat,
		Radius:     float64(radius),
		RadiusUnit: radiusUnit,
	}).Result()

	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	userIds := make([]uint64, 0, len(members))
	for i := range members {
		userId, err := cacheKeyToUserId(members[i])
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		userIds = append(userIds, userId)
	}
	return userIds, nil
}

func (h *Cache) SearchUsersWithLocationsNearby(ctx context.Context,
	loc cache.Location, radius uint64) ([]cache.UserIdWithLocation, error) {
	sourceFunc := h.SearchUsersWithLocationsNearby

	geoLocs, err := h.rdb.GeoSearchLocation(ctx, keyUserLocations, &redis.GeoSearchLocationQuery{
		GeoSearchQuery: redis.GeoSearchQuery{
			Longitude:  loc.Lon,
			Latitude:   loc.Lat,
			Radius:     float64(radius),
			RadiusUnit: radiusUnit,
		},
		WithCoord: true, // !
	}).Result()

	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	userIdAndLocList := []cache.UserIdWithLocation{}
	for i := range geoLocs {
		userId, err := cacheKeyToUserId(geoLocs[i].Name)
		if err != nil {
			return nil, utl.NewFuncError(sourceFunc, err)
		}

		userIdAndLocList = append(userIdAndLocList, cache.UserIdWithLocation{
			UserId: userId,
			Loc: cache.Location{
				Lon: geoLocs[i].Longitude,
				Lat: geoLocs[i].Latitude,
			},
		})
	}
	return userIdAndLocList, nil
}

func (h *Cache) RemoveAllForUser(ctx context.Context, userId uint64) error {
	sourceFunc := h.RemoveAllForUser
	cacheKey := makeCacheKeyForUserId(userId)

	err := utl.RunFuncsRetErr(
		func() error {
			return h.rdb.ZRem(ctx, keyUserLocations, cacheKey).Err()
		},
		func() error {
			return h.rdb.Del(ctx, cacheKey).Err()
		})
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil // ok
}
