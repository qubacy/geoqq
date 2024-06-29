package rabbit

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"errors"
	"log"

	"common/pkg/rabbitUtils"

	"github.com/wagslane/go-rabbitmq"
)

type InputParams struct {
	rabbitUtils.ConnectionParams
	ExchangeName string
	QueueName    string
}

// -----------------------------------------------------------------------

type Rabbit struct {
	conn     *rabbitmq.Conn
	consumer *rabbitmq.Consumer

	// services
}

func New(startCtx context.Context, params InputParams) (*Rabbit, error) {
	conn, err := rabbitmq.NewConn(
		params.CreateConnectionString(),
		rabbitmq.WithConnectionOptionsLogging)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	// ***

	consumer, err := rabbitmq.NewConsumer(conn,
		params.QueueName,
		rabbitmq.WithConsumerOptionsRoutingKey(""),
		rabbitmq.WithConsumerOptionsExchangeName(params.ExchangeName),
		rabbitmq.WithConsumerOptionsExchangeDeclare)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	go func() {
		if err := consumer.Run(messageHandler); err != nil {
			logger.Warning("%v", utl.NewFuncError(New, err)) //
		}
	}()

	// ***

	return &Rabbit{
		conn:     conn,
		consumer: consumer,
	}, nil
}

func (r *Rabbit) Stop(ctx context.Context) error {
	r.consumer.Close() // no error!

	var errs = []error{}
	if err := r.conn.Close(); err != nil {
		errs = append(errs, utl.NewFuncError(r.Stop, err))
	}

	return errors.Join(errs...)
}

// -----------------------------------------------------------------------

func messageHandler(d rabbitmq.Delivery) (action rabbitmq.Action) {
	log.Printf("consumed: %v", string(d.Body))

	return rabbitmq.Ack
}
