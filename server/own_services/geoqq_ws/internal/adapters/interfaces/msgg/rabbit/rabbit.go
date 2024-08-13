package rabbit

import (
	"common/pkg/logger"
	"common/pkg/messaging/geoqq"
	"common/pkg/messaging/geoqq/dto"
	"common/pkg/messaging/geoqq/dto/payload"
	"common/pkg/rabbitUtils"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"errors"
	"geoqq_ws/internal/application/ports/input"
	"time"

	"github.com/wagslane/go-rabbitmq"
)

type InputParams struct {
	rabbitUtils.ConnectionParams
	ExchangeName string
	QueueName    string

	HandleTimeout time.Duration

	// ***

	MateRequestUc input.MateRequestUsecase
	MateChatUc    input.MateChatUsecase
	MateMessageUc input.MateMessageUsecase
	GeoMessageUc  input.GeoMessageUsecase
	PublicUserUc  input.PublicUserUsecase
}

// -----------------------------------------------------------------------

type Rabbit struct {
	conn     *rabbitmq.Conn
	consumer *rabbitmq.Consumer

	// services

	handleTimeout time.Duration

	mateRequestUc input.MateRequestUsecase
	mateChatUc    input.MateChatUsecase
	mateMessageUc input.MateMessageUsecase
	geoMessageUc  input.GeoMessageUsecase
	publicUserUc  input.PublicUserUsecase
}

func New(startCtx context.Context, params *InputParams) (*Rabbit, error) {
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

	instance := &Rabbit{
		conn:          conn,
		consumer:      consumer,
		handleTimeout: params.HandleTimeout,

		mateRequestUc: params.MateRequestUc,
		mateChatUc:    params.MateChatUc,
		mateMessageUc: params.MateMessageUc,
		geoMessageUc:  params.GeoMessageUc,
		publicUserUc:  params.PublicUserUc,
	}

	go func() {
		if err := consumer.Run(instance.messageHandler); err != nil {
			logger.Warning("%v", utl.NewFuncError(New, err)) //
		}
	}()

	return instance, nil
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

func (r *Rabbit) messageHandler(d rabbitmq.Delivery) (action rabbitmq.Action) {
	sourceFunc := r.messageHandler
	action = rabbitmq.Ack // ok!
	var err error

	msg := dto.Message{}
	if err = json.Unmarshal(d.Body, &msg); err != nil {
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		return
	}

	// ***

	switch msg.Event {

	case geoqq.EventUpdatedPublicUser:
		err = r.handleUpdatedPublicUser(msg.Payload)

	case geoqq.EventAddedMateChat:
		err = r.handleAddedMateChat(msg.Payload)
	case geoqq.EventUpdatedMateChat:
		err = r.handleUpdatedMateChat(msg.Payload)

	case geoqq.EventAddedMateRequest:
		err = r.handleAddedMateRequest(msg.Payload)
	case geoqq.EventAddedMateMessage:
		err = r.handleAddedMateMessage(msg.Payload)

	case geoqq.EventAddedGeoMessage:
		err = r.handleAddedGeoMessage(msg.Payload)
	}

	if err != nil {
		logger.Error("%v", utl.NewFuncError(sourceFunc, err))
		return
	}

	return
}

// specific
// -----------------------------------------------------------------------

func (r *Rabbit) handleUpdatedPublicUser(pd any) error {
	sourceFunc := r.handleUpdatedPublicUser
	onlyId, err := dto.PayloadFromAny[payload.OnlyId](pd) // TODO: change in the future!
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), r.handleTimeout)
	defer cancel()

	err = r.publicUserUc.InformAboutPublicUserUpdated(ctx, uint64(onlyId.Id))
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil // ok
}

// -----------------------------------------------------------------------

func (r *Rabbit) handleAddedMateChat(pd any) error {
	sourceFunc := r.handleAddedMateChat
	err := r.handleMateChatEvent(pd,
		func(ctx context.Context, targetWithId payload.TargetWithId) error {

			return r.mateChatUc.InformAboutMateChatAdded(ctx,
				uint64(targetWithId.TargetUserId),
				uint64(targetWithId.Id))
		})

	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

func (r *Rabbit) handleUpdatedMateChat(pd any) error {
	sourceFunc := r.handleUpdatedMateChat
	err := r.handleMateChatEvent(pd,
		func(ctx context.Context, targetWithId payload.TargetWithId) error {

			// similar code?

			return r.mateChatUc.InformAboutMateChatUpdated(ctx,
				uint64(targetWithId.TargetUserId),
				uint64(targetWithId.Id))
		})

	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

// ***

type handleMateChatSpecEvent = func(
	ctx context.Context, targetWithId payload.TargetWithId) error

func (r *Rabbit) handleMateChatEvent(pd any, handleSpecEvent handleMateChatSpecEvent) error {
	sourceFunc := r.handleMateChatEvent
	targetWithId, err := dto.PayloadFromAny[payload.TargetWithId](pd) // TODO: change in the future!
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), r.handleTimeout)
	defer cancel()

	// ***

	{
		err = handleSpecEvent(ctx, targetWithId)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}
	}

	return nil // ok!
}

// -----------------------------------------------------------------------

func (r *Rabbit) handleAddedMateRequest(pd any) error {
	sourceFunc := r.handleAddedMateRequest
	mr, err := dto.PayloadFromAny[payload.MateRequest](pd)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	ctx, cancel := context.WithTimeout(context.Background(), r.handleTimeout)
	defer cancel()

	err = r.mateRequestUc.ForwardMateRequest(ctx,
		uint64(mr.UserId), uint64(mr.TargetUserId), uint64(mr.Id))
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil // ok!
}

func (r *Rabbit) handleAddedMateMessage(pd any) error {
	sourceFunc := r.handleAddedMateMessage
	mm, err := dto.PayloadFromAny[payload.MateMessage](pd)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	ctx, cancel := context.WithTimeout(context.Background(), r.handleTimeout)
	defer cancel()

	// payload object to domain?

	err = r.mateMessageUc.ForwardMateMessage(ctx,
		uint64(mm.TargetUserId), mm.ToDomain())
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}

// -----------------------------------------------------------------------

func (r *Rabbit) handleAddedGeoMessage(pd any) error {
	sourceFunc := r.handleAddedGeoMessage
	gm, err := dto.PayloadFromAny[payload.GeoMessage](pd)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), r.handleTimeout)
	defer cancel()

	err = r.geoMessageUc.ForwardGeoMessage(ctx, gm.ToDomain(),
		gm.Longitude, gm.Latitude)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil // ok
}
