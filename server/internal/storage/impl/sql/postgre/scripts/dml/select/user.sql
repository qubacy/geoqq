SELECT * FROM "UserEntry";
SELECT * FROM "UserDetails";
SELECT * FROM "UserOptions";
SELECT * FROM "UserLocation";

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "UserEntry"
    WHERE "Username" = 'Test';

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count" FROM "UserEntry"
    WHERE "Username" = 'Test' AND
          "HashPassword" = '878172e71ca3b32ad5acb7c2a9ffea20465f93e4';

-- -----------------------------------------------------------------------

SELECT "Id" FROM "UserEntry"
    WHERE "Username" = 'Joshua' 
        AND "HashPassword" = '7C4A8D09CA3762AF61E59520943DC26494F8941B';

-- -----------------------------------------------------------------------

SELECT "Id" FROM "UserEntry"
    WHERE "Username" = 'Joshua';

-- 2024-01-14T18:34:08.411053
-- 2024-01-14T18:35:04.848421
