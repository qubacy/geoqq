package cache

import "context"

type CacheManager interface {
	Get(ctx context.Context, key string) string
}
