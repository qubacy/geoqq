package rabbit

import (
	"common/pkg/messaging/geoqq"
	"common/pkg/messaging/geoqq/dto"
	"common/pkg/messaging/geoqq/dto/payload"
	"common/pkg/rabbitUtils"
	utl "common/pkg/utility"
	"context"
	"encoding/json"
	"log"
	"os"
	"strconv"
	"strings"
	"testing"
	"time"

	"github.com/docker/go-connections/nat"
	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/rabbitmq"

	rmq "github.com/wagslane/go-rabbitmq"
)

var (
	rabbitmqContainer *rabbitmq.RabbitMQContainer
	rabbitmqUsername  = "guest"
	rabbitmqPassword  = "guest"

	rabbitmqHost string
	rabbitmqPort uint16

	publisher *rmq.Publisher
	rabbit    *Rabbit

	exchangeName = "geoqq"
	queueName    = "events"
)

// -----------------------------------------------------------------------

func TestMain(m *testing.M) {
	setup()
	rc := m.Run()
	teardown()

	os.Exit(rc)
}

func setup() {
	var err error
	startCtx := context.Background()

	err = utl.RunFuncsRetErr(
		func() error {
			rabbitmqContainer, err = rabbitmq.RunContainer(
				startCtx,
				testcontainers.WithImage("rabbitmq:3.13.3-management-alpine"),
				rabbitmq.WithAdminUsername(rabbitmqUsername),
				rabbitmq.WithAdminPassword(rabbitmqPassword),
			)
			if err != nil {
				return err
			}

			time.Sleep(1 * time.Second)
			return nil
		},
		func() error {
			portEndpoint, err := rabbitmqContainer.PortEndpoint(
				startCtx, nat.Port(rabbitmq.DefaultAMQPPort), "")
			if err != nil {
				return err
			}

			parts := strings.Split(portEndpoint, ":")
			rabbitmqHost = parts[0]
			portAsU64, _ := strconv.ParseUint(parts[1], 10, 64)
			rabbitmqPort = uint16(portAsU64)

			return nil
		})
	if err != nil {
		log.Fatalf("rabbitmq container err: %s", err)
	}

	connParams := rabbitUtils.ConnectionParams{
		Host:     rabbitmqHost,
		Port:     rabbitmqPort,
		Username: rabbitmqUsername,
		Password: rabbitmqPassword,
	}

	// lib

	if err = initPublisher(connParams); err != nil {
		log.Fatal(err)
	}

	// with consumer

	rabbit, err = New(startCtx, &InputParams{
		ConnectionParams: rabbitUtils.ConnectionParams{
			Host:     rabbitmqHost,
			Port:     rabbitmqPort,
			Username: rabbitmqUsername,
			Password: rabbitmqPassword,
		},
		ExchangeName: exchangeName,
		QueueName:    queueName,
	})
	if err != nil {
		log.Fatal(err)
	}
}

func teardown() {
	stopCtx := context.Background()
	if err := rabbit.Stop(stopCtx); err != nil {
		log.Fatal(err)
	}
}

func initPublisher(params rabbitUtils.ConnectionParams) error {
	var err error = nil

	conn, err := rmq.NewConn(
		params.CreateConnectionString(),
		rmq.WithConnectionOptionsLogging)
	if err != nil {
		return utl.NewFuncError(initPublisher, err)
	}

	publisher, err = rmq.NewPublisher(
		conn,
		rmq.WithPublisherOptionsLogging,
		rmq.WithPublisherOptionsExchangeName(exchangeName),
		rmq.WithPublisherOptionsExchangeDeclare)
	if err != nil {
		return utl.NewFuncError(initPublisher, err)
	}
	return nil
}

// -----------------------------------------------------------------------

func Test_SendPublicUser(t *testing.T) {
	msg := dto.Message{
		Event: geoqq.EventUpdatedPublicUser,
		Payload: payload.OnlyId{
			Id: 1001,
		},
	}
	jsonBytes, err := json.Marshal(msg)
	if err != nil {
		t.Error(err)
		return
	}

	if err = publisher.Publish(jsonBytes, []string{}); err != nil {
		t.Error(err)
		return
	}
	time.Sleep(1 * time.Second)
}
