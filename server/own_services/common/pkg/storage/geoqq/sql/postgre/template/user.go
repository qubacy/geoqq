package template

import (
	utl "common/pkg/utility"
)

var (
	/*
		Order:
			1. lon
			2. lat
			3. userId
	*/
	UpdateUserLocation = utl.RemoveAdjacentWs(`
		UPDATE "UserLocation" 
			SET "Longitude" = $1, "Latitude" = $2,
				"Time" = NOW()::timestamp
		WHERE "UserId" = $3`)
)
