package redisCache

import (
	"context"
	"fmt"

	"common/pkg/logger"
	utl "common/pkg/utility"
	"time"

	"github.com/redis/go-redis/v9"
)

type Dependencies struct {
	Host     string
	Port     uint16
	User     string
	Password string
	DbIndex  int
}

// -----------------------------------------------------------------------

type RedisCache struct {
	client *redis.Client
}

func New(ctxForInit context.Context, deps Dependencies) (*RedisCache, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     fmt.Sprintf("%v:%v", deps.Host, deps.Port),
		Username: deps.User,
		Password: deps.Password,
		DB:       deps.DbIndex,
	})

	statusCmd := client.Ping(ctxForInit)
	if err := statusCmd.Err(); err != nil {
		logger.Debug("response to ping from redis `%v`", statusCmd.String())
		return nil, utl.NewFuncError(New, err)
	}

	return &RedisCache{
		client: client,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (s *RedisCache) Set(ctx context.Context, key string, value string) error {
	statusCmd := s.client.Set(ctx, key, value, 0)
	if err := statusCmd.Err(); err != nil {
		return utl.NewFuncError(s.Set, err)
	}
	return nil
}

func (s *RedisCache) SetWithTTL(ctx context.Context, key, value string,
	ttl time.Duration) error {
	statusCmd := s.client.SetEx(ctx, key, value, ttl)
	if err := statusCmd.Err(); err != nil {
		return utl.NewFuncError(s.SetWithTTL, err)
	}
	return nil
}

func (s *RedisCache) Exists(ctx context.Context, key string) (bool, error) {
	intCmd := s.client.Exists(ctx, key)
	count, err := intCmd.Result()
	if err != nil {
		return false, utl.NewFuncError(s.Exists, err)
	}
	return count > 0, nil
}

func (s *RedisCache) Get(ctx context.Context, key string) (string, error) {
	stringCmd := s.client.Get(ctx, key)
	value, err := stringCmd.Result()
	if err != nil {
		return "", utl.NewFuncError(s.Get, err)
	}
	return value, nil
}

func (s *RedisCache) StringLength(ctx context.Context, key string) (int64, error) {
	intCmd := s.client.StrLen(ctx, key)
	length, err := intCmd.Result()
	if err != nil {
		return 0, utl.NewFuncError(s.StringLength, err)
	}
	return length, nil
}

func (s *RedisCache) Del(ctx context.Context, key string) error {
	if err := s.client.Del(ctx, key).Err(); err != nil {
		return utl.NewFuncError(s.Del, err)
	}
	return nil
}
