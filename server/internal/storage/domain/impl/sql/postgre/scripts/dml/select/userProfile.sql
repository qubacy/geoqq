SELECT 
	"UserEntry"."Id" AS "Id",
	"Username", 
	"Description",
	"AvatarId",
	"HitMeUp"
FROM "UserEntry"
INNER JOIN "UserDetails"
	ON "UserDetails"."UserId" = "UserEntry"."Id"
INNER JOIN "UserOptions" 
	ON "UserOptions"."UserId" = "UserEntry"."Id";