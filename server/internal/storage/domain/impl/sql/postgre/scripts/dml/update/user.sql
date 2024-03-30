UPDATE "UserLocation"
SET "Longitude" = 123.123,
    "Latitude" = 123.123
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

UPDATE "UserEntry"
SET "HashUpdToken" = '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
    "SignInTime" = NOW()::timestamp
WHERE "Id" = 1;

SELECT * FROM "UserEntry";

-- -----------------------------------------------------------------------

UPDATE "UserDetails"
SET "Description" = 'New Desc'
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

SELECT *
FROM "UserDetails";


SELECT *
FROM "Avatar";


UPDATE "UserDetails"
SET "AvatarId" = 1
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

SELECT * FROM "UserEntry";

UPDATE "UserEntry"
SET "Username" = 'test_user'
WHERE "Id" = 13;


-- -----------------------------------------------------------------------

SELECT *
FROM "UserOptions";


UPDATE "UserOptions"
SET "HitMeUp" = 1
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

SELECT *
FROM "UserEntry";


UPDATE "UserEntry"
SET "HashPassword" = '648b5cdceb22b83601efc41b8f692f62288fcd4c'
WHERE "Id" = 13;

-- -----------------------------------------------------------------------

SELECT * FROM "UserLocation";