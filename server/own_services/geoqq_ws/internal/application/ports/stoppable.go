package ports

import "context"

type Stoppable interface {
	Stop(ctx context.Context) error
}
