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

	GetUserLocationWithId = utl.RemoveAdjacentWs(`
		SELECT * FROM "UserLocation"
			WHERE "UserId" = $1`)

	/*
		Order:
			1. userId
			2. lon
			3. lat
	*/
	InsertUserLocationNoReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "UserLocation" (
			"UserId", 
			"Longitude",
			"Latitude",
			"Time"
		) 
		VALUES (
			$1, $2, $3,
			NOW()::timestamp)`)  // returning id?
)
