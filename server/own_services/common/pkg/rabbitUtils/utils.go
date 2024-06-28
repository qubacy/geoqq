package rabbitUtils

import "fmt"

type ConnectionParams struct {
	Username string
	Password string
	Host     string
	Port     uint16
}

func (params *ConnectionParams) CreateConnectionString() string {
	return fmt.Sprintf("amqp://%v:%v@%v:%v",
		params.Username, params.Password,
		params.Host, params.Port,
	)
}
