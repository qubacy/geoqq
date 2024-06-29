package rabbit

import (
	"common/pkg/rabbitUtils"
	"common/pkg/utility"
	"context"
	"log"
	"os"
	"strconv"
	"strings"
	"testing"

	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/rabbitmq"
)

var (
	rabbitmqContainer *rabbitmq.RabbitMQContainer
	rabbitmqUsername  = "rabbit"
	rabbitmqPassword  = "rabbit"

	rabbitmqHost string
	rabbitmqPort uint16

	rabbit *Rabbit
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

	err = utility.RunFuncsRetErr(
		func() error {
			rabbitmqContainer, err = rabbitmq.RunContainer(
				startCtx,
				testcontainers.WithImage("rabbitmq:3.13.3-management-alpine"),
				rabbitmq.WithAdminUsername(rabbitmqUsername),
				rabbitmq.WithAdminPassword(rabbitmqPassword))
			return err
		},
		func() error {
			var endpoint string
			endpoint, err = rabbitmqContainer.Endpoint(startCtx, "")
			if err != nil {
				return err
			}

			parts := strings.Split(endpoint, ":")
			rabbitmqHost = parts[0]
			portAsU64, _ := strconv.ParseUint(parts[1], 10, 64)
			rabbitmqPort = uint16(portAsU64)

			return nil
		})
	if err != nil {
		log.Fatalf("rabbitmq container err: %s", err)
	}

	// ***

	rabbit, err = New(startCtx, InputParams{
		ConnectionParams: rabbitUtils.ConnectionParams{
			Host:     rabbitmqHost,
			Port:     rabbitmqPort,
			Username: rabbitmqUsername,
			Password: rabbitmqPassword,
		},
		ExchangeName: "geoqq",
		QueueName:    "events",
	})
	if err != nil {
		log.Fatal(err)
	}
}

func teardown() {
	stopCtx := context.Background()
	rabbit.Stop(stopCtx)
}

// -----------------------------------------------------------------------

// -----------------------------------------------------------------------
