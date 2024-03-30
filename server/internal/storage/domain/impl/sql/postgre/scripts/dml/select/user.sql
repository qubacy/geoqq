SELECT *
FROM "UserEntry";

UPDATE "UserEntry" 
SET "HashPassword" = 'adf20ec5664499c609ad141057e9bf94212eb337' WHERE "Id" = 1;


SELECT *
FROM "UserDetails";


SELECT *
FROM "UserOptions";


SELECT *
FROM "UserLocation";

SELECT * FROM "UserEntry"
INNER JOIN "UserDetails" ON "UserEntry"."Id" = "UserDetails"."UserId"
INNER JOIN "UserOptions" ON "UserEntry"."Id" = "UserOptions"."UserId"
INNER JOIN "UserLocation" ON "UserEntry"."Id" = "UserLocation"."UserId";

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Username" = 'Test';


SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Id" = 1;

select
    case when exists (select true
    from "UserEntry"
    where "Id" = 3)
    then true
    else false
end;

select true
from "UserEntry"
where "Id" = 2;

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Username" = 'Test'
    AND "HashPassword" = '878172e71ca3b32ad5acb7c2a9ffea20465f93e4';

-- -----------------------------------------------------------------------

SELECT "Id"
FROM "UserEntry"
WHERE "Username" = 'Joshua'
    AND "HashPassword" = '7C4A8D09CA3762AF61E59520943DC26494F8941B';

-- -----------------------------------------------------------------------

SELECT "Id"
FROM "UserEntry"
WHERE "Username" = 'Joshua';

-- 2024-01-14T18:34:08.411053
-- 2024-01-14T18:35:04.848421
-- -----------------------------------------------------------------------

SELECT "HashUpdToken"
FROM "UserEntry"
WHERE "Id" = 1;

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
WHERE "Id" = 1
    AND "HashUpdToken" = '1A455338CFA6D1D6AB3A92367769216706A673F4';

-- -----------------------------------------------------------------------

SELECT "Username",
    "Description",
    "AvatarId",
    "HitMeUp"
FROM "UserEntry"
    LEFT JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
    LEFT JOIN "UserOptions" ON "UserOptions"."UserId" = "UserEntry"."Id"
WHERE "UserEntry"."Id" = 1;

-- -----------------------------------------------------------------------

SELECT *
FROM "UserEntry";


SELECT COUNT(*)
FROM "UserEntry"
WHERE "Id" = 2
    AND "HashPassword" = '7c4a8d09ca3762af61e59520943dc26494f8941b';


