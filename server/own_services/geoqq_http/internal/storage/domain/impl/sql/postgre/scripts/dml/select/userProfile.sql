-- GetUserProfile
-- -----------------------------------------------------------------------

SELECT * FROM "UserEntry";
SELECT * FROM "UserDetails";

SELECT 
	"Id",
	"Login",
	"Username", 
	"Description",
	"AvatarId",
	"HitMeUp"
FROM "UserEntry"
INNER JOIN "UserDetails"
	ON "UserDetails"."UserId" = "Id"
INNER JOIN "UserOptions" 
	ON "UserOptions"."UserId" = "Id"
WHERE "Id" = 3;