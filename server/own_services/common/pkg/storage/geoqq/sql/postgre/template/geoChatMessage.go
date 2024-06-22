package template

import (
	utl "common/pkg/utility"
)

var (
	/*
		Order:
			1. fromUserId
			2. text
			3. lat
			4. lon
	*/
	InsertGeoChatMessage = utl.RemoveAdjacentWs(`
		INSERT INTO "GeoMessage" (
			"FromUserId", "Text", "Time",
			"Latitude", "Longitude")
		VALUES ($1, $2, NOW()::timestamp, $3, $4) 
			RETURNING "Id"`)

	SelectGeoChatMessage = utl.RemoveAdjacentWs(`
		SELECT "Id", "FromUserId", "Text", "Time" 
			FROM "GeoMessage" WHERE "Id" = $1`)
)
