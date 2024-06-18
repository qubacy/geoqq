package template

import (
	utl "common/pkg/utility"
)

var (
	InsertMateChatWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "MateChat" (
			"FirstUserId", "SecondUserId")
		VALUES($1, $2) ON CONFLICT (
			GREATEST("FirstUserId", "SecondUserId"), 
			LEAST("FirstUserId", "SecondUserId"))
		DO UPDATE SET "CreationOrReTime" = NOW()::timestamp`)  // see index `unique_mate_chat_ids_comb`

	/*
		Conflicts will be ignored.

		Order:
			1. firstUserId
			2. secondUserId
	*/
	InsertMateChat = InsertMateChatWithoutReturningId +
		` RETURNING "Id"`
)
