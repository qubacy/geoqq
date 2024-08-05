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

	/*
		Order:
			1. userId
	*/
	GetAllMateChatsForUser = utl.RemoveAdjacentWs(`
		SELECT "MateChat"."Id" AS "Id",
			case
				when "FirstUserId" = $1 
				then "SecondUserId" else "FirstUserId"
			end as "UserId",
			"CreationOrReTime",

			(SELECT COUNT(*) FROM "MateMessage"
			WHERE "MateChatId" = "MateChat"."Id"
				AND "MateMessage"."FromUserId" != $1 
				AND "Read" = false) AS "NewMessageCount",

			case
				when "LastMessage"."Id" is NULL 
				then false else true
			end as "Exists",

			"LastMessage"."Id" AS "LastMessageId",
			"LastMessage"."Text" AS "LastMessageText",
			"LastMessage"."Time" AS "LastMessageTime",
			"LastMessage"."FromUserId" AS "LastMessageUserId",
			"LastMessage"."Read" AS "Read"

		FROM "MateChat"
		LEFT JOIN LATERAL
			(SELECT *
			FROM "MateMessage"
			WHERE "MateChatId" = "MateChat"."Id"
			ORDER BY "Time" DESC
			LIMIT 1) "LastMessage" ON true
		LEFT JOIN "DeletedMateChat" ON 
			("DeletedMateChat"."ChatId" = "MateChat"."Id"
				AND "DeletedMateChat"."UserId" = $1)
		WHERE ("FirstUserId" = $1 OR "SecondUserId" = $1) AND 
			"DeletedMateChat"."ChatId" IS NULL`)

	/*
		Order:
			1. userId
			2. count
			3. offset
	*/
	GetMateChatsForUser = GetAllMateChatsForUser +
		` ORDER BY "LastMessageTime" DESC NULLS LAST,
			   "LastMessageId" DESC NULLS LAST /* ? */
		LIMIT $2 OFFSET $3`

	/*
		Order:
			1. userId
			2. chatId
	*/
	GetMateChatWithIdForUser = GetAllMateChatsForUser +
		` AND "MateChat"."Id" = $2`
)
