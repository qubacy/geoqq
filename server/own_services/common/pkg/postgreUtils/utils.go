package postgreUtils

import (
	"fmt"
)

type ConnectionParamsWithDb struct {
	Username     string
	Password     string
	Host         string
	Port         uint16
	DatabaseName string
}

func (params *ConnectionParamsWithDb) CreateConnectionString() string {
	return fmt.Sprintf(
		"user=%v password=%v "+
			"host=%v port=%v database=%v",
		params.Username, params.Password,
		params.Host, params.Port,
		params.DatabaseName,
	)
}

func CreateConnectionString(params ConnectionParamsWithDb) string {
	return params.CreateConnectionString()
}
