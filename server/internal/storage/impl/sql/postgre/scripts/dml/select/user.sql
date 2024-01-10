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