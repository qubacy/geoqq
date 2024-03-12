SELECT *
FROM "GeoMessage";


SELECT public.geodistance(
    55.75222, 37.61556,
    56.01839, 92.86717
    );

-- -----------------------------------------------------------------------
WITH "srcLat" AS (VALUES (56.01839)),
     "srcLon" AS (VALUES (92.86717))
SELECT "Id",
       "Text",
       "Time",
       "FromUserId" AS "UserId",
       (SELECT geodistance(
            "Latitude", "Longitude",
            (table "srcLat"),
            (table "srcLon"))
            ) AS "Distance"
FROM "GeoMessage"
ORDER BY "Time" DESC;

-- all
-- -----------------------------------------------------------------------

WITH "srcLat" AS (VALUES (55.75222)),
     "srcLon" AS (VALUES (92.86717)),
     "distance" AS (VALUES (1000))
SELECT "Id",
       "Text",
       "Time",
       "FromUserId" AS "UserId"
FROM "GeoMessage"
WHERE geodistance(
    "Latitude", "Longitude",
    (table "srcLat"),
    (table "srcLon")) 
        < (table "distance")
ORDER BY "Time" DESC;

-- with limit and offset
-- -----------------------------------------------------------------------

WITH "srcLat" AS (VALUES (55.75222)),
     "srcLon" AS (VALUES (92.86717)),
     "distance" AS (VALUES (1000))
SELECT "Id",
       "Text",
       "Time",
       "FromUserId" AS "UserId"
FROM "GeoMessage"
WHERE geodistance(
    "Latitude", "Longitude",
    (table "srcLat"),
    (table "srcLon")) 
        < (table "distance")
ORDER BY "Time" DESC
LIMIT 1
OFFSET 0;
