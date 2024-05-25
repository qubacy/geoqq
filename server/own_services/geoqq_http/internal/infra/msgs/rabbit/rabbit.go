package rabbit

import (
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"fmt"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/infra/msgs"
	"geoqq_http/internal/infra/msgs/dto"

	"github.com/wagslane/go-rabbitmq"
)

const (
	contentType = "application/json"
)

// -----------------------------------------------------------------------

type InputParams struct {
	Username string
	Password string
	Host     string
	Port     uint16

	ExchangeName string
}

func createUrl(params InputParams) string {
	return fmt.Sprintf("amqp://%v:%v@%v:%v",
		params.Username, params.Password,
		params.Host, params.Port,
	)
}

// -----------------------------------------------------------------------

type Rabbit struct {
	conn         *rabbitmq.Conn
	publisher    *rabbitmq.Publisher
	exchangeName string
}

func New(ctxForCancel context.Context, params InputParams) (*Rabbit, error) {
	conn, err := rabbitmq.NewConn(
		createUrl(params),
		rabbitmq.WithConnectionOptionsLogging,
	)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	// ***

	publisher, err := rabbitmq.NewPublisher(
		conn,
		rabbitmq.WithPublisherOptionsLogging,
		rabbitmq.WithPublisherOptionsExchangeName(params.ExchangeName),
		rabbitmq.WithPublisherOptionsExchangeDeclare,
	)
	if err != nil {
		return nil, utl.NewFuncError(New, err)
	}

	// ***

	return &Rabbit{
		conn:         conn,
		publisher:    publisher,
		exchangeName: params.ExchangeName,
	}, nil
}

// public
// -----------------------------------------------------------------------

func (r *Rabbit) SendPublicUser(ctx context.Context, event string, userId uint64) error {
	pu := dto.PublicUser{Id: float64(userId)}
	msg := dto.Message{Event: event, Payload: &pu}

	msgBytes, err := json.Marshal(msg)
	if err != nil {
		return utl.NewFuncError(r.SendPublicUser, err)
	}

	err = r.publisher.Publish(
		msgBytes, []string{event},
		rabbitmq.WithPublishOptionsContentType(contentType),
		rabbitmq.WithPublishOptionsExchange(r.exchangeName),
	)
	if err != nil {
		return utl.NewFuncError(r.SendPublicUser, err)
	}

	return nil // ok
}

func (r *Rabbit) SendMateChat(ctx context.Context, event string,
	targetUserId uint64, mc *domain.MateChat) error {
	return msgs.ErrNotImplemented
}

func (r *Rabbit) SendMateRequest(ctx context.Context, event string,
	targetUserId, requestId, requesterUserId uint64) error {
	return msgs.ErrNotImplemented
}

func (r *Rabbit) SendMateMessage(ctx context.Context, event string,
	targetUserId uint64, chatId uint64, mm *domain.MateMessage) error {
	return msgs.ErrNotImplemented
}

func (r *Rabbit) SendGeoMessage(ctx context.Context, event string,
	lat, lon float64, gm *domain.GeoMessage) error {
	return msgs.ErrNotImplemented
}

// private
// -----------------------------------------------------------------------
