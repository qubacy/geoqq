package cache

import (
	"context"
	"time"
)

type Cache interface {
	Set(ctx context.Context, key, value string) error
	SetWithTTL(ctx context.Context, key, value string,
		ttl time.Duration) error

	Get(ctx context.Context, key string) (string, error)
	Exists(ctx context.Context, key string) (bool, error)
	StringLength(ctx context.Context, key string) (int64, error)

	Del(ctx context.Context, key string) error
}
