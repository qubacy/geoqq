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

	HasUserWithId = utl.RemoveAdjacentWs(`
		SELECT COUNT(*) AS "Count" FROM "UserEntry"
			WHERE "Id" = $1`)

	/*
		Order:
			1. userId
	*/
	UpdateLastActionTimeForUser = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry" SET "LastActionTime" = NOW()::timestamp
			WHERE "Id" = $1`)
)
