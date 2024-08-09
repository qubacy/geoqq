package template

import (
	utl "common/pkg/utility"
)

var (
	InsertMateWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "Mate" ("FirstUserId", "SecondUserId")
		VALUES ($1, $2) 
			ON CONFLICT DO NOTHING`)

	InsertMate = `` +
		InsertMateWithoutReturningId + ` RETURNING "Id"`

	/*
		Order:
			1. userId
	*/
	GetMateIdsForUser = utl.RemoveAdjacentWs(`
		SELECT
			case when "FirstUserId" = $1
				then "SecondUserId" else "FirstUserId"
				END as "UserId"
		FROM "Mate"
		WHERE ("FirstUserId" = $1 OR "SecondUserId" = $1)`)
)
