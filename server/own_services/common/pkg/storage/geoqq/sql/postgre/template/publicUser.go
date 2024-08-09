package template

import (
	utl "common/pkg/utility"
)

var (
	/*
		Order:
			1. source userId
			2. target userId (or some ids)

		To complete the request you need to add:
			- = $2
			- IN (...)
	*/
	SelectPublicUsers = utl.RemoveAdjacentWs(`
		SELECT 
			"UserEntry"."Id" AS "Id",
			"UserDetails"."Username" AS "Username", /* public */
			"Description",
			"AvatarId",
			"LastActionTime",
			case
				when "Mate"."Id" is null then false
				else true
			end as "IsMate",
			case 
				when "DeletedUser"."UserId" is null then false
				else true
			end as "IsDeleted",
			"UserOptions"."HitMeUp" AS "HitMeUp"
		FROM "UserEntry"
		INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
		INNER JOIN "UserOptions" ON "UserOptions"."UserId" = "UserEntry"."Id"
		LEFT JOIN "Mate" ON (
			("Mate"."FirstUserId" = $1 AND
				"Mate"."SecondUserId" = "UserEntry"."Id") OR
        	("Mate"."FirstUserId" = "UserEntry"."Id" AND
				"Mate"."SecondUserId" = $1)
		)
		LEFT JOIN "DeletedUser" ON "DeletedUser"."UserId" = "UserEntry"."Id"
			WHERE "UserEntry"."Id"`) // next placeholders start with 2.
)
