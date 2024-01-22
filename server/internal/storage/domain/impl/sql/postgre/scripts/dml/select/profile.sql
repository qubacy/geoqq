SELECT 
	"Username", 
	"Description",
	"AvatarId",
	"HitMeUp"
FROM "UserEntry"
LEFT JOIN "UserDetails"
	ON "UserDetails"."UserId" = "UserEntry"."Id"
LEFT JOIN "UserOptions" 
	ON "UserOptions"."UserId" = "UserEntry"."Id";