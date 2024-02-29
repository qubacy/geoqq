SELECT *
FROM "UserEntry";

SELECT *
FROM "Mate";

-- -----------------------------------------------------------------------

WITH "srcUserId" AS (VALUES(1)),
     "targetUserId" AS (VALUES(2))
SELECT
    "UserEntry"."Id" AS "Id",
    "Username",
    "Description",
    "AvatarId",
    case
           when "Mate"."Id" is null then false
           else true
       end as "IsMate"
FROM "UserEntry"
    INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
    LEFT JOIN "Mate" ON (
			("Mate"."FirstUserId" = (table "srcUserId") AND "Mate"."SecondUserId" = "UserEntry"."Id") OR
        ("Mate"."FirstUserId" = "UserEntry"."Id" AND "Mate"."SecondUserId" = (table "srcUserId"))
		)
WHERE "UserEntry"."Id" = (table "targetUserId");

-- -----------------------------------------------------------------------

WITH "srcUserId" AS (VALUES(1))
SELECT "Username", "Description", "AvatarId",
    case when "Mate"."Id" is null then false else true end as "IsMate"
FROM "UserEntry"
    INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
    LEFT JOIN "Mate" ON (
			("Mate"."FirstUserId" = (table "srcUserId") AND "Mate"."SecondUserId" = "UserEntry"."Id") OR
        ("Mate"."FirstUserId" = "UserEntry"."Id" AND "Mate"."SecondUserId" = (table "srcUserId"))
		)
WHERE "UserEntry"."Id" IN (1, 2, 3, 4, 5)

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Id" IN (3,
               4);

