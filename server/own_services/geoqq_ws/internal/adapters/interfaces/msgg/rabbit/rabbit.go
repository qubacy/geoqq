package rabbit

import (
	utl "common/pkg/utility"
	"context"

	"common/pkg/rabbitUtils"

	"github.com/wagslane/go-rabbitmq"
)

type InputParams struct {
	rabbitUtils.ConnectionParams
	ExchangeName string
}

// -----------------------------------------------------------------------

type Rabbit struct {
	conn *rabbitmq.Conn
}

func New(startCtx context.Context, params InputParams) (*Rabbit, error) {
	conn, err := rabbitmq.NewConn(
		params.CreateConnectionString(),
		rabbitmq.WithConnectionOptionsLogging)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	// ***

	return &Rabbit{
		conn: conn,
	}, nil
}

func (r *Rabbit) Stop(ctx context.Context) error {
	if err := r.conn.Close(); err != nil {
		return utl.NewFuncError(r.Stop, err)
	}

	return nil
}
