SELECT "Id",
       "Username",
       "Description",
       "AvatarId"
FROM "UserEntry"
INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id";

-- -----------------------------------------------------------------------

