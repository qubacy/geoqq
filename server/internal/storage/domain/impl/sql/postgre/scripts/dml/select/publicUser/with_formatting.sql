WITH "srcUserId" AS (
                     VALUES (1)),
     "targetUserId" AS (
                        VALUES (3))
SELECT "Username",
       "Description",
       "AvatarId",
       case
           when "Mate"."Id" is null then false
           else true
       end as "IsMate"
FROM "UserEntry"
INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
LEFT JOIN "Mate" ON "Mate"."FirstUserId" = (table "srcUserId")
AND "Mate"."SecondUserId" = (table "targetUserId")
WHERE "UserEntry"."Id" = (table "srcUserId");

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Id" IN (3,
               4);

