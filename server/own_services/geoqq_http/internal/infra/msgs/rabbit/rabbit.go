package rabbit

import (
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"fmt"
	"geoqq_http/internal/domain"
	"geoqq_http/internal/infra/msgs"
	"geoqq_http/internal/infra/msgs/rabbit/dto"
	"geoqq_http/internal/infra/msgs/rabbit/dto/payload"
	"time"

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
	MessageTtl   time.Duration
}

func createUrl(params InputParams) string {
	return fmt.Sprintf("amqp://%v:%v@%v:%v",
		params.Username, params.Password,
		params.Host, params.Port,
	)
}

// -----------------------------------------------------------------------

type Rabbit struct {
	conn             *rabbitmq.Conn
	publisher        *rabbitmq.Publisher
	exchangeName     string
	messageTtl       time.Duration
	messageTtlOption string
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
		conn:             conn,
		publisher:        publisher,
		exchangeName:     params.ExchangeName,
		messageTtl:       params.MessageTtl, // source...
		messageTtlOption: fmt.Sprintf("%v", params.MessageTtl.Milliseconds()),
	}, nil
}

// public
// -----------------------------------------------------------------------

func (r *Rabbit) SendPublicUserId(ctx context.Context, event string, userId uint64) error {
	sourceFunc := r.SendPublicUserId

	oid := payload.OnlyId{Id: float64(userId)}
	msg := dto.Message{Event: event, Payload: &oid}

	if err := r.publishWithBasicOptions(ctx, &msg, []string{event}); err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil // ok
}

func (r *Rabbit) SendMateChatId(ctx context.Context, event string,
	targetUserId, chatId uint64) error {
	sourceFunc := r.SendMateChatId

	twid := payload.TargetWithId{
		TargetUserId: float64(targetUserId),
		Id:           float64(chatId)}
	msg := dto.Message{Event: event, Payload: &twid}

	if err := r.publishWithBasicOptions(ctx, &msg, []string{event}); err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

func (r *Rabbit) SendMateRequest(ctx context.Context, event string,
	targetUserId, requestId, requesterUserId uint64) error {
	sourceFunc := r.SendMateRequest

	mr := payload.MateRequest{
		TargetUserId: float64(targetUserId),
		Id:           float64(requestId),
		UserId:       float64(requesterUserId), // from!
	}
	msg := dto.Message{Event: event, Payload: &mr}

	if err := r.publishWithBasicOptions(ctx, &msg, []string{event}); err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

// -----------------------------------------------------------------------

func (r *Rabbit) SendMateMessage(ctx context.Context, event string,
	targetUserId uint64, chatId uint64, domainMm *domain.MateMessage) error {
	sourceFunc := r.SendMateMessage

	mm := payload.MateMessage{
		TargetUserId: float64(targetUserId),
		Id:           float64(domainMm.Id),
		ChatId:       float64(chatId),
		Text:         domainMm.Text,
		Time:         float64(domainMm.Time.Unix()),
		UserId:       float64(domainMm.UserId),
		Read:         domainMm.Read,
	}
	msg := dto.Message{Event: event, Payload: &mm}

	if err := r.publishWithBasicOptions(ctx, &msg, []string{event}); err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

func (r *Rabbit) SendGeoMessage(ctx context.Context, event string,
	lat, lon float64, domainGm *domain.GeoMessage) error {
	sourceFunc := r.SendGeoMessage

	gm := payload.GeoMessage{
		Id:        float64(domainGm.Id),
		Text:      domainGm.Text,
		Time:      float64(domainGm.Time.Unix()),
		UserId:    float64(domainGm.UserId),
		Latitude:  lat,
		Longitude: lon,
	}
	msg := dto.Message{Event: event, Payload: &gm}

	if err := r.publishWithBasicOptions(ctx, &msg, []string{event}); err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return msgs.ErrNotImplemented
}

// private
// -----------------------------------------------------------------------

func (r *Rabbit) publishWithBasicOptions(ctx context.Context,
	msg *dto.Message, routingKeys []string) error {

	sourceFunc := r.publishWithBasicOptions
	data, err := json.Marshal(msg)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	err = r.publisher.PublishWithContext(
		ctx,
		data, routingKeys,

		rabbitmq.WithPublishOptionsExchange(r.exchangeName),
		rabbitmq.WithPublishOptionsContentType(contentType),
		rabbitmq.WithPublishOptionsTimestamp(time.Now().UTC()),
		rabbitmq.WithPublishOptionsExpiration(r.messageTtlOption),
	)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	return nil
}
