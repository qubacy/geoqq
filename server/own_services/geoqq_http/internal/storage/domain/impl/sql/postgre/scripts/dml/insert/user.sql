SELECT * FROM "UserEntry";
SELECT * FROM "UserDetails";

-- InsertUser (parts)
-- -----------------------------------------------------------------------

INSERT INTO "UserEntry" ("Login",
                         "HashPassword",
                         "HashUpdToken",
                         "SignUpTime",
                         "SignInTime",
                         "LastActionTime")
VALUES ('Test6',
        '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
        '12e63aa178b4aea553a7e9c0fa4d253405325109',
        NOW()::timestamp, 
        NOW()::timestamp,
        NOW()::timestamp
        ) RETURNING "Id";

-- -----------------------------------------------------------------------
-- skipped HashUpdToken...

SELECT * FROM "UserEntry";

INSERT INTO "UserEntry" ("Login",
                         "HashPassword",
                         "SignUpTime",
                         "SignInTime",
                         "LastActionTime")
VALUES ('Test2',
        '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
        NOW()::timestamp,
        NOW()::timestamp,
        NOW()::timestamp
        ) RETURNING "Id";

-- -----------------------------------------------------------------------

INSERT INTO "UserLocation" ("UserId",
                            "Longitude",
                            "Latitude",
                            "Time")
VALUES (3,
        100.100,
        100.100,
        NOW()::timestamp);

-- -----------------------------------------------------------------------
 -- skipped Description

INSERT INTO "UserDetails" ("UserId",
                           "Username",
                           "AvatarId")
VALUES (3, 'Test', 1);

-- -----------------------------------------------------------------------

INSERT INTO "UserOptions" ("UserId",
                           "HitMeUp")
VALUES (3, 0);
