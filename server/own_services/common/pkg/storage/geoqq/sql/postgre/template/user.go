package template

import (
	utl "common/pkg/utility"
)

var (
	/*
		Order:
			1. login
			2. passwordDoubleHash
	*/
	InsertUserEntryWithoutHashUpdToken = utl.RemoveAdjacentWs(`
		INSERT INTO "UserEntry" (
			"Login", "HashPassword",
			"SignUpTime", "SignInTime",
			"LastActionTime"
			)
		VALUES (
			$1, $2,
			NOW()::timestamp, 
			NOW()::timestamp,
			NOW()::timestamp
		) RETURNING "Id"`)

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
