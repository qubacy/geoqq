package template

import (
	utl "common/pkg/utility"
)

var (
	InsertMateChatMessageWithoutReturningId = utl.RemoveAdjacentWs(`
		INSERT INTO "MateMessage" (
			"MateChatId", "FromUserId",
			"Text", "Time", "Read")
		VALUES ($1, $2, $3, 
			NOW()::timestamp, FALSE)`)

	/*
		Order:
			1. mateChatId
			2. fromUserId
			3. text
	*/
	InsertMateChatMessage = `` +
		InsertMateChatMessageWithoutReturningId +
		` RETURNING "Id"`
)
