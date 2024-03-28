WITH "UserIds" ("srcUserId", "targetUserId") 
    AS (VALUES (1, 2));
                                            
-- -----------------------------------------------------------------------

WITH "srcUserId" AS (VALUES (1)),
     "targetUserId" AS (VALUES (3))

SELECT 
       "Username",
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

-- not working!
DO $$

DECLARE
    source_user_id BIGINT;
    target_user_id BIGINT;

BEGIN
    source_user_id := 1;
    target_user_id := 2;

SELECT
    "Username",
    "Description",
    "AvatarId",
    case when "Mate"."Id" is null then false else true end as "IsMate"
FROM "UserEntry"
    INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
    LEFT JOIN "Mate" ON "Mate"."FirstUserId" = source_user_id AND
        "Mate"."SecondUserId" = target_user_id
WHERE "UserEntry"."Id" = source_user_id;

END $$

-- -----------------------------------------------------------------------

SELECT 
    "UserEntry"."Id" AS "Id",
    "Username", "Description",
    "AvatarId",
    case when "Mate"."Id" is null then false else true end as "IsMate"
FROM "UserEntry"
INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
LEFT JOIN "Mate" ON (
    ("Mate"."FirstUserId" = 1 AND "Mate"."SecondUserId" = "UserEntry"."Id") OR
    ("Mate"."FirstUserId" = "UserEntry"."Id" AND "Mate"."SecondUserId" = 1)
)
WHERE "UserEntry"."Id" = 5;

-- -----------------------------------------------------------------------

WITH "srcUserId" AS (VALUES (1)),
     "targetUserId" AS (VALUES (3))
SELECT 
    "UserEntry"."Id" AS "Id",
    case 
        when "DeletedUser"."UserId" is null then false
        else true
    end as "IsDeleted",
    "Username",
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
LEFT JOIN "DeletedUser" ON "DeletedUser"."UserId" = "UserEntry"."Id"
WHERE "UserEntry"."Id" = (table "srcUserId");

-- -----------------------------------------------------------------------

SELECT * FROM "DeletedUser";
SELECT * FROM "Mate";