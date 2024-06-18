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
)
