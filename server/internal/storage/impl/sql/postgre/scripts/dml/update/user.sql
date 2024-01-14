UPDATE "UserLocation" 
SET 
    "Longitude" = 123.123, 
    "Latitude" = 123.123
WHERE "UserId" = 1;

-- -----------------------------------------------------------------------

-- CREATE TABLE "UserEntry"
-- (
--     "Id" BIGSERIAL PRIMARY KEY NOT NULL,
--     "Username" CHARACTER VARYING(128) UNIQUE NOT NULL,
--     "HashPassword" VARCHAR(512) NOT NULL,
--     "HashUpdToken" VARCHAR(512) NOT NULL DEFAULT '',
--     "SignUpTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
--     "SignInTime" TIMESTAMP WITHOUT TIME ZONE
-- );


UPDATE "UserEntry"
    SET "HashUpdToken" = '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
        "SignInTime" = NOW()::timestamp
    WHERE "Id" = 1;
