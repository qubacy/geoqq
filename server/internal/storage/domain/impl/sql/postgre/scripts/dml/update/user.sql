UPDATE "UserLocation"
SET "Longitude" = 123.123,
    "Latitude" = 123.123
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

UPDATE "UserEntry"
SET "HashUpdToken" = '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
    "SignInTime" = NOW()::timestamp
WHERE "Id" = 1;

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

SELECT *
FROM "UserOptions";


UPDATE "UserOptions"
SET "HitMeUp" = 1
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

SELECT *
FROM "UserEntry";


UPDATE "UserEntry"
SET "HashPassword" = '7c4a8d09ca3762af61e59520943dc26494f8941b'
WHERE "Id" = 1;

-- -----------------------------------------------------------------------