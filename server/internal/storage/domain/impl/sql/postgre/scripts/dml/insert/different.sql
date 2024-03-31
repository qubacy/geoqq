SELECT * FROM "UserEntry";

INSERT INTO "UserEntry" ("Username",
                         "HashPassword",
                         "HashUpdToken",
                         "SignUpTime",
                         "SignInTime",
                         "LastActionTime")
VALUES ('Test',
        '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
        '12e63aa178b4aea553a7e9c0fa4d253405325109',
        NOW()::timestamp, 
        NOW()::timestamp,
        NOW()::timestamp
        ) RETURNING "Id";

-- -----------------------------------------------------------------------
 -- skipped HashUpdToken

INSERT INTO "UserEntry" ("Username",
                         "HashPassword",
                         "SignUpTime",
                         "SignInTime")
VALUES ('Test',
        '878172e71ca3b32ad5acb7c2a9ffea20465f93e4',
        NOW()::timestamp,
        NULL) RETURNING "Id";

-- -----------------------------------------------------------------------

INSERT INTO "UserLocation" ("UserId",
                            "Longitude",
                            "Latitude",
                            "Time")
VALUES (1,
        100.100,
        100.100,
        NOW()::timestamp);

-- -----------------------------------------------------------------------
 -- skipped Description

INSERT INTO "UserDetails" ("UserId")
VALUES (1);

-- -----------------------------------------------------------------------

INSERT INTO "UserOptions" ("UserId",
                           "HitMeUp")
VALUES (1,
        0);

-- -----------------------------------------------------------------------
