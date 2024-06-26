package background

import (
	"geoqq_ws/internal/application/ports"
)

type Database interface {
	ports.Stoppable

	UserDatabase
}
